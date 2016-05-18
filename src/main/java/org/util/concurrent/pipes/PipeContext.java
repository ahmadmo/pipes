package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public final class PipeContext {

    private final Pipe pipe;
    private final PipelineContext pipelineContext;
    private final Channel channel = new ConcurrentChannel();

    PipeContext(Pipe pipe, PipelineContext pipelineContext) {
        this.pipe = pipe;
        this.pipelineContext = pipelineContext;
    }

    public Pipe pipe() {
        return pipe;
    }

    public PipelineContext pipelineContext() {
        return pipelineContext;
    }

    public Channel channel() {
        return channel;
    }

    public Pipeline pipeline() {
        return pipelineContext.pipeline();
    }

    public boolean writeToChannel(int pipeIndex, Object message) {
        return pipelineContext.channel(pipeIndex).write(message);
    }

    public DataBus dataBus() {
        return pipelineContext.dataBus();
    }

    public EventBus eventBus() {
        return pipelineContext.eventBus();
    }

}
