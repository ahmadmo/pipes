/*
 * Copyright 2016 Ahmad Mozafarnia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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