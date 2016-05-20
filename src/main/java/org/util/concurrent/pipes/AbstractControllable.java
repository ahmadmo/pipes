/*
 * Copyright 2016 Ahmad Mozafarnia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.util.concurrent.pipes;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ahmad
 */
abstract class AbstractControllable extends ReadWriteLockContainer implements Controllable {

    private final String name;
    private final AtomicBoolean paused = new AtomicBoolean();
    private final AtomicBoolean stopped = new AtomicBoolean();
    private final AtomicBoolean shutdown = new AtomicBoolean();
    private final AtomicReference<CountDownLatch> latchHolder = new AtomicReference<>();

    AbstractControllable(String name) {
        this.name = name;
    }

    @Override
    public Object start() {
        return start(true);
    }

    Object start(Object... args) {
        return acquireReadLock(() -> {
            checkShutdown();
            paused.set(false);
            stopped.set(false);
            return doStart(args);
        });
    }

    abstract Object doStart(Object... args);

    @Override
    public void pause() {
        acquireWriteLock(() -> {
            checkShutdown();
            checkStopped();
            if (paused.compareAndSet(false, true)) {
                latchHolder.set(new CountDownLatch(1));
            }
        });
    }

    @Override
    public boolean isPaused() {
        return paused.get();
    }

    @Override
    public void resume() {
        acquireWriteLock(() -> {
            checkShutdown();
            checkStopped();
            if (paused.compareAndSet(true, false)) {
                latchHolder.getAndSet(null).countDown();
            }
        });
    }

    @Override
    public boolean awaitAndContinue() throws InterruptedException {
        if (stopped.get()) {
            return false;
        }
        CountDownLatch latch = latchHolder.get();
        if (latch != null) {
            latch.await();
        }
        return !stopped.get();
    }

    @Override
    public boolean awaitAndContinueUninterruptibly() {
        boolean result = false;
        boolean interrupted = false;
        try {
            result = awaitAndContinue();
        } catch (InterruptedException e) {
            interrupted = true;
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return result;
    }

    @Override
    public void stop() {
        acquireWriteLock(() -> {
            checkShutdown();
            stop0();
        });
    }

    @Override
    public boolean isStopped() {
        return stopped.get();
    }

    @Override
    public void shutdown() {
        acquireWriteLock(() -> {
            stop0();
            if (shutdown.compareAndSet(false, true)) {
                doShutdown();
            }
        });
    }

    private void stop0() {
        if (stopped.compareAndSet(false, true)) {
            CountDownLatch latch = latchHolder.getAndSet(null);
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    abstract void doShutdown();

    @Override
    public boolean isShutdown() {
        return shutdown.get();
    }

    void checkStopped() {
        if (stopped.get()) {
            throw new IllegalStateException(name + " is stopped.");
        }
    }

    void checkShutdown() {
        if (shutdown.get()) {
            throw new IllegalStateException(name + " has been shut down.");
        }
    }

}
