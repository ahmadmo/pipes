package org.util.concurrent.pipes;

import org.util.concurrent.futures.Do;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ahmad
 */
public final class Pipeline {

    private static final Handler<PipeException> DEFAULT_EXCEPTION_HANDLER = e -> {
        throw e;
    };

    private final List<Pipe> pipes = new ArrayList<>();
    private final Handler<PipeException> exceptionHandler;

    private Pipeline(Handler<PipeException> exceptionHandler) {
        this.exceptionHandler = exceptionHandler == null ? DEFAULT_EXCEPTION_HANDLER : exceptionHandler;
    }

    List<Pipe> getPipes() {
        return pipes;
    }

    public Pipe pipeAt(int pipeIndex) {
        return pipes.get(pipeIndex);
    }

    public int size() {
        return pipes.size();
    }

    public PipelineFuture start() {
        return start(PipelineContext.shared());
    }

    public PipelineFuture start(final PipelineContext pipelineContext) {
        return new PipelinePromiseImpl(
                this,
                pipes.stream()
                        .map(pipe -> new PipePromiseImpl(
                                pipe, pipe.isBlocking()
                                ? Do.runSerial(runnable(pipe, pipelineContext))
                                : Do.runAsync(runnable(pipe, pipelineContext))
                        ))
                        .collect(Collectors.toList())
        );
    }

    private Runnable runnable(Pipe pipe, PipelineContext pipelineContext) {
        final PipeContext context = new PipeContext(pipe, this, pipelineContext);
        return () -> {
            try {
                pipe.getProcess().start(context);
            } catch (Throwable cause) {
                exceptionHandler.handle(new PipeException(cause, context));
            }
        };
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
