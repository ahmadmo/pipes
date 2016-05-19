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
public final class Pipe {

    final long id = Seq.next();

    private final int index;
    private final String name;
    private final Process process;
    private final boolean blocking;

    Pipe(int index, String name, Process process, boolean blocking) {
        this.index = index;
        this.name = name;
        this.process = process;
        this.blocking = blocking;
    }

    public int index() {
        return index;
    }

    public String name() {
        return name;
    }

    public Process process() {
        return process;
    }

    public boolean isBlocking() {
        return blocking;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Pipe && id == ((Pipe) obj).id;
    }

    @Override
    public String toString() {
        return "Pipe{" +
                "id=" + id +
                ", index=" + index +
                ", name='" + name + '\'' +
                ", blocking=" + blocking +
                '}';
    }

}
