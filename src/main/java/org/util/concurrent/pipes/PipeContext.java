package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public final class PipeContext {

    private final Pipe pipe;
    private final Pipeline pipeline;
    private final PipelineContext pipelineContext;

    PipeContext(Pipe pipe, Pipeline pipeline, PipelineContext pipelineContext) {
        this.pipe = pipe;
        this.pipeline = pipeline;
        this.pipelineContext = pipelineContext;
    }

    public Pipe pipe() {
        return pipe;
    }

    public boolean writeToChannel(int pipeIndex, Object message) {
        return pipeline.pipeAt(pipeIndex).getChannel().write(message);
    }

    public DataBus dataBus() {
        return pipelineContext.getDataBus();
    }

    public EventBus eventBus() {
        return pipelineContext.getEventBus();
    }

}
