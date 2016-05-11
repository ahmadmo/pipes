package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public final class PipeContext {

    private final Pipe pipe;
    private final Pipeline pipeline;

    PipeContext(Pipe pipe, Pipeline pipeline) {
        this.pipe = pipe;
        this.pipeline = pipeline;
    }

    public Pipe pipe() {
        return pipe;
    }

    public DataBus dataBus() {
        return pipeline.getDataBus();
    }

    public EventBus eventBus() {
        return pipeline.getEventBus();
    }

    public boolean writeToChannel(int pipeIndex, Object message) {
        return pipeline.pipeAt(pipeIndex).getChannel().write(message);
    }

}
