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

import java.util.*;

/**
 * @author ahmad
 */
public final class Pipeline {

    final long id = Seq.next();

    private final List<Pipe> pipes;
    private final Map<String, Pipe> pipeNames;

    private Pipeline(List<Pipe> pipes, Map<String, Pipe> pipeNames) {
        this.pipes = pipes;
        this.pipeNames = pipeNames;
    }

    public List<Pipe> pipes() {
        return Collections.unmodifiableList(pipes);
    }

    public Pipe pipeAt(int pipeIndex) {
        return pipes.get(pipeIndex);
    }

    public Pipe findPipe(String name) {
        return pipeNames.get(name);
    }

    public int size() {
        return pipes.size();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Pipeline && id == ((Pipeline) obj).id;
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "id=" + id +
                ", pipes=" + pipes +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final List<Pipe> pipes = new ArrayList<>();
        private final Map<String, Pipe> pipeNames = new HashMap<>();

        public Builder next(Process process) {
            return next("pipe-" + pipes.size(), process);
        }

        public Builder next(String name, Process process) {
            return next(name, process, true);
        }

        public Builder nextAsync(Process process) {
            return nextAsync("pipe-" + pipes.size(), process);
        }

        public Builder nextAsync(String name, Process process) {
            return next(name, process, false);
        }

        private Builder next(String name, Process process, boolean blocking) {
            Pipe pipe = new Pipe(pipes.size(), name, process, blocking);
            if (pipeNames.put(name, pipe) != null) {
                throw new IllegalStateException("Duplicate pipe name : " + name);
            }
            pipes.add(pipe);
            return this;
        }

        public Pipeline build() {
            return new Pipeline(pipes, pipeNames);
        }

    }

}
