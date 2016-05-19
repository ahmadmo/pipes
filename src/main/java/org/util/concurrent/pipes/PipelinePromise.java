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

import org.util.concurrent.futures.Promise;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author ahmad
 */
public interface PipelinePromise extends PipelineFuture, Promise<Void> {

    @Override
    List<PipePromise> pipeFutures();

    @Override
    PipePromise pipeFutureAt(int pipeIndex);

    @Override
    PipePromise findPipeFuture(String pipeName);

    @Override
    PipelinePromise whenComplete(BiConsumer<? super Void, ? super Throwable> action);

    @Override
    PipelinePromise exceptionally(Function<Throwable, ? extends Void> function);

}
