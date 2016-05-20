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

import java.util.concurrent.CompletableFuture;

/**
 * @author ahmad
 */
public final class CompletablePipe extends Completable<CompletablePipe> {

    private final Pipe pipe;

    CompletablePipe(Pipe pipe, CompletableFuture<Void> future) {
        super(future);
        this.pipe = pipe;
    }

    public Pipe pipe() {
        return pipe;
    }

}
