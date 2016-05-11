package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public final class PipeException extends RuntimeException {

    private static final long serialVersionUID = -352070621720781361L;

    private final PipeContext pipeContext;

    public PipeException(PipeContext pipeContext) {
        this.pipeContext = pipeContext;
    }

    public PipeException(String message, PipeContext pipeContext) {
        super(message);
        this.pipeContext = pipeContext;
    }

    public PipeException(String message, Throwable cause, PipeContext pipeContext) {
        super(message, cause);
        this.pipeContext = pipeContext;
    }

    public PipeException(Throwable cause, PipeContext pipeContext) {
        super(cause);
        this.pipeContext = pipeContext;
    }

    public PipeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, PipeContext pipeContext) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.pipeContext = pipeContext;
    }

    public PipeContext getPipeContext() {
        return pipeContext;
    }

}
