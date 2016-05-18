package org.util.concurrent.pipes;

/**
 * @author ahmad
 */
public final class PipeContext {

    private final Pipe pipe;
    private final PipelineContext pipelineContext;
    private final Channel channel = new ConcurrentChannel();
    private final Channel readOnlyChannel = channel.readOnly();
    private final Channel writeOnlyChannel = channel.writeOnly();

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

    public Channel readOnlyChannel() {
        return readOnlyChannel;
    }

    public Channel writeOnlyChannel() {
        return writeOnlyChannel;
    }

    public Pipeline pipeline() {
        return pipelineContext.pipeline();
    }

    public boolean writeToChannel(int pipeIndex, Object message) {
        return pipelineContext.writeOnlyChannel(pipeIndex).write(message);
    }

    public boolean writeToChannel(String pipeName, Object message) {
        return pipelineContext.writeOnlyChannel(pipeName).write(message);
    }

    public DataBus dataBus() {
        return pipelineContext.dataBus();
    }

    public EventBus eventBus() {
        return pipelineContext.eventBus();
    }

}
