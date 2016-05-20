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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

/**
 * @author ahmad
 */
final class Futures {

    private Futures() {
    }

    @SuppressWarnings("unchecked")
    static <V> CompletableFuture<V> combine(List<? extends CompletableFuture<? extends V>> futures,
                                            BiFunction<? super V, ? super V, ? extends V> combiner) {
        if (futures.isEmpty()) {
            return new CompletableFuture<>();
        }
        final CompletableFuture[] future = {null};
        for (CompletableFuture<? extends V> c : futures) {
            future[0] = future[0] == null ? c : future[0].thenCombine(c, combiner);
        }
        for (CompletableFuture<? extends V> c : futures) {
            c.exceptionally(ex -> {
                future[0].completeExceptionally(ex);
                return null;
            });
        }
        return future[0];
    }

    static CompletableFuture<Void> combine(List<? extends CompletableFuture<? extends Void>> futures) {
        final CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        for (CompletableFuture<?> child : futures) {
            child.exceptionally(ex -> {
                future.completeExceptionally(ex);
                return null;
            });
        }
        return future;
    }

}
