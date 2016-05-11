package org.util.concurrent.pipes;

import org.util.concurrent.futures.Do;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ahmad
 */
public final class Pipeline {

    private static final Handler<PipeException> DEFAULT_EXCEPTION_HANDLER = e -> {
        throw e;
    };

    private final List<Pipe> pipes = new ArrayList<>();
    private final DataBus dataBus = new DataBusImpl();
    private final EventBus eventBus = new EventBusImpl();
    private final Handler<PipeException> exceptionHandler;

    private Pipeline(Handler<PipeException> exceptionHandler) {
        this.exceptionHandler = exceptionHandler == null ? DEFAULT_EXCEPTION_HANDLER : exceptionHandler;
    }

    List<Pipe> getPipes() {
        return pipes;
    }

    Pipe pipeAt(int pipeIndex) {
        return pipes.get(pipeIndex);
    }

    public int size() {
        return pipes.size();
    }

    public DataBus getDataBus() {
        return dataBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public PipelineFuture start() {
        List<PipePromise> promises = new ArrayList<>();
        for (final Pipe pipe : pipes) {
            Runnable r = () -> start(pipe);
            promises.add(new PipePromiseImpl(pipe,
                    pipe.isBlocking() ? Do.runSerial(r) : Do.runAsync(r))
            );
        }
        return new PipelinePromiseImpl(this, promises);
    }

    private void start(Pipe pipe) {
        PipeContext context = new PipeContext(pipe, this);
        try {
            pipe.getProcess().start(context);
        } catch (Throwable cause) {
            exceptionHandler.handle(new PipeException(cause, context));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final List<ProcessInfo> processes = new ArrayList<>();
        private Handler<PipeException> exceptionHandler;

        public Builder next(Process process) {
            processes.add(new ProcessInfo(process, true));
            return this;
        }

        public Builder nextAsync(Process process) {
            processes.add(new ProcessInfo(process, false));
            return this;
        }

        public Builder exceptionHandler(Handler<PipeException> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public Pipeline build() {
            Pipeline pipeline = new Pipeline(exceptionHandler);
            for (int i = 0, n = processes.size(); i < n; i++) {
                ProcessInfo info = processes.get(i);
                pipeline.pipes.add(new Pipe(i, info.process, new ConcurrentChannel(), info.blocking));
            }
            return pipeline;
        }

        private static final class ProcessInfo {

            private final Process process;
            private final boolean blocking;

            private ProcessInfo(Process process, boolean blocking) {
                this.process = process;
                this.blocking = blocking;
            }

        }

    }

}
