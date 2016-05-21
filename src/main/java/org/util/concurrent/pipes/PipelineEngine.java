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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ahmad
 */
public final class PipelineEngine extends AbstractControllable {

    private final ThreadPool pool = new ThreadPool();

    public PipelineEngine() {
        super("PipelineEngine");
    }

    public CompletablePipeline start(Pipeline pipeline) {
        return start(pipeline, false);
    }

    public CompletablePipeline start(Pipeline pipeline, boolean shared) {
        return start(pipeline, shared, null);
    }

    public CompletablePipeline start(Pipeline pipeline, String contextName) {
        return start(pipeline, false, contextName);
    }

    private CompletablePipeline start(Pipeline pipeline, boolean shared, String contextName) {
        return (CompletablePipeline) start(new Object[]{pipeline, shared, contextName});
    }

    @Override
    Object doStart(Object... args) {
        Pipeline pipeline = (Pipeline) args[0];
        boolean shared = (boolean) args[1];
        String contextName = (String) args[2];
        final PipelineContext pipelineContext = shared ? PipelineContext.shared(pipeline)
                : contextName == null ? PipelineContext.create(pipeline)
                : PipelineContext.named(pipeline, contextName);
        List<CompletablePipe> completablePipes = new ArrayList<>();
        Map<String, CompletablePipe> completablePipeNames = new HashMap<>();
        for (Pipe pipe : pipeline.pipes()) {
            CompletablePipe pipePromise = new CompletablePipe(pipe, pipe.isBlocking()
                    ? pool.runSerial(new RunnablePipe(pipelineContext.pipeContext(pipe), this))
                    : pool.runAsync(new RunnablePipe(pipelineContext.pipeContext(pipe), this))
            );
            completablePipes.add(pipePromise);
            completablePipeNames.put(pipe.name(), pipePromise);
        }
        return new CompletablePipeline(
                pipeline, completablePipes, completablePipeNames,
                Futures.combine(completablePipes.stream().map(Completable::future).collect(Collectors.toList())),
                this
        );
    }

    @Override
    void doShutdown() {
        pool.shutdownGracefully();
    }

}
