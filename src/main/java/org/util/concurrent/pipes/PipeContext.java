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

    public void startProcess() {
        pipe.process().start(this);
    }

}
