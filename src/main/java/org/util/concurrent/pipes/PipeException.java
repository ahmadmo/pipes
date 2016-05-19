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
public final class PipeException extends RuntimeException {

    private static final long serialVersionUID = -352070621720781361L;

    private final PipeContext pipeContext;

    public PipeException(PipeContext pipeContext) {
        this.pipeContext = pipeContext;
    }

    public PipeException(String message, PipeContext pipeContext) {
        super(message);
        this.pipeContext = pipeContext;
    }

    public PipeException(String message, Throwable cause, PipeContext pipeContext) {
        super(message, cause);
        this.pipeContext = pipeContext;
    }

    public PipeException(Throwable cause, PipeContext pipeContext) {
        super(cause);
        this.pipeContext = pipeContext;
    }

    public PipeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, PipeContext pipeContext) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.pipeContext = pipeContext;
    }

    public PipeContext getPipeContext() {
        return pipeContext;
    }

}
