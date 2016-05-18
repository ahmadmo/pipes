package org.util.concurrent.pipes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ahmad
 */
public final class PipelineContext {

    private static final class SharedContext {
        private static final PipelineContext INSTANCE = new PipelineContext();
    }

    private static final ConcurrentMap<String, PipelineContext> NAMED_CONTEXTS = new ConcurrentHashMap<>();

    private final DataBus dataBus = new DataBusImpl();
    private final EventBus eventBus = new EventBusImpl();

    public DataBus getDataBus() {
        return dataBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public static PipelineContext shared() {
        return SharedContext.INSTANCE;
    }

    public static PipelineContext create() {
        return new PipelineContext();
    }

    public static PipelineContext named(String name) {
        return NAMED_CONTEXTS.computeIfAbsent(name, s -> new PipelineContext());
    }

}