package org.util.concurrent.pipes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ahmad
 */
public final class PipelineContext {

    private static final ConcurrentMap<Long, PipelineContext> SHARED_CONTEXTS = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, ConcurrentMap<String, PipelineContext>> NAMED_CONTEXTS = new ConcurrentHashMap<>();

    private final Pipeline pipeline;
    private final DataBus dataBus = new DataBusImpl();
    private final EventBus eventBus = new EventBusImpl();
    private final ConcurrentMap<Long, PipeContext> pipeContexts = new ConcurrentHashMap<>();

    private PipelineContext(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Pipeline pipeline() {
        return pipeline;
    }

    public DataBus dataBus() {
        return dataBus;
    }

    public EventBus eventBus() {
        return eventBus;
    }

    PipeContext pipeContext(Pipe pipe) {
        return pipeContexts.computeIfAbsent(pipe.id, l -> new PipeContext(pipe, this));
    }

    public Channel writeOnlyChannel(int pipeIndex) {
        return pipeContext(pipeline.pipeAt(pipeIndex)).writeOnlyChannel();
    }

    public Channel writeOnlyChannel(String pipeName) {
        return pipeContext(pipeline.findPipe(pipeName)).writeOnlyChannel();
    }

    public static PipelineContext create(Pipeline pipeline) {
        return new PipelineContext(pipeline);
    }

    public static PipelineContext shared(final Pipeline pipeline) {
        return SHARED_CONTEXTS.computeIfAbsent(pipeline.id, l -> new PipelineContext(pipeline));
    }

    public static PipelineContext named(final Pipeline pipeline, String name) {
        return NAMED_CONTEXTS.computeIfAbsent(pipeline.id, l -> new ConcurrentHashMap<>())
                .computeIfAbsent(name, s -> new PipelineContext(pipeline));
    }

}