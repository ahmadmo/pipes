package org.util.concurrent.pipes;

import org.util.concurrent.futures.Do;

import java.util.*;

/**
 * @author ahmad
 */
public final class Pipeline {

    private static final Handler<PipeException> DEFAULT_EXCEPTION_HANDLER = e -> {
        throw e;
    };

    final long id = Seq.next();

    private final List<Pipe> pipes;
    private final Map<String, Pipe> pipeNames;
    private final Handler<PipeException> exceptionHandler;

    private Pipeline(List<Pipe> pipes, Map<String, Pipe> pipeNames, Handler<PipeException> exceptionHandler) {
        this.pipes = pipes;
        this.pipeNames = pipeNames;
        this.exceptionHandler = exceptionHandler == null ? DEFAULT_EXCEPTION_HANDLER : exceptionHandler;
    }

    public List<Pipe> pipes() {
        return Collections.unmodifiableList(pipes);
    }

    public Pipe pipeAt(int pipeIndex) {
        return pipes.get(pipeIndex);
    }

    public Pipe findPipe(String name) {
        return pipeNames.get(name);
    }

    public int size() {
        return pipes.size();
    }

    public PipelineFuture start() {
        return start(false);
    }

    public PipelineFuture start(boolean shared) {
        return start(shared ? PipelineContext.shared(this) : PipelineContext.create(this));
    }

    public PipelineFuture start(String contextName) {
        return start(PipelineContext.named(this, contextName));
    }

    public PipelineFuture start(final PipelineContext pipelineContext) {
        List<PipePromise> pipePromises = new ArrayList<>();
        Map<String, PipePromise> pipePromiseNames = new HashMap<>();
        for (Pipe pipe : pipes) {
            Runnable runnable = runnable(pipe, pipelineContext);
            PipePromise pipePromise = new PipePromiseImpl(pipe, pipe.isBlocking() ? Do.runSerial(runnable) : Do.runAsync(runnable));
            pipePromises.add(pipePromise);
            pipePromiseNames.put(pipe.name(), pipePromise);
        }
        return new PipelinePromiseImpl(this, pipePromises, pipePromiseNames);
    }

    private Runnable runnable(Pipe pipe, PipelineContext pipelineContext) {
        final PipeContext pipeContext = pipelineContext.pipeContext(pipe);
        return () -> {
            try {
                pipe.process().start(pipeContext);
            } catch (Throwable cause) {
                exceptionHandler.handle(new PipeException(cause, pipeContext));
            }
        };
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Pipeline && id == ((Pipeline) obj).id;
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "id=" + id +
                ", pipes=" + pipes +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final List<Pipe> pipes = new ArrayList<>();
        private final Map<String, Pipe> pipeNames = new HashMap<>();
        private Handler<PipeException> exceptionHandler;

        public Builder next(Process process) {
            return next("pipe-" + pipes.size(), process);
        }

        public Builder next(String name, Process process) {
            return next(name, process, true);
        }

        public Builder nextAsync(Process process) {
            return nextAsync("pipe-" + pipes.size(), process);
        }

        public Builder nextAsync(String name, Process process) {
            return next(name, process, false);
        }

        private Builder next(String name, Process process, boolean blocking) {
            Pipe pipe = new Pipe(pipes.size(), name, process, blocking);
            if (pipeNames.put(name, pipe) != null) {
                throw new IllegalStateException("Duplicate pipe name : " + name);
            }
            pipes.add(pipe);
            return this;
        }

        public Builder exceptionHandler(Handler<PipeException> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public Pipeline build() {
            return new Pipeline(pipes, pipeNames, exceptionHandler);
        }

    }

}
