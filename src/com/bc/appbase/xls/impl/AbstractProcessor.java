/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.appbase.xls.impl;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Chinomso Bassey Ikwuagwu on May 9, 2017 1:11:35 PM
 * @param <A> The type of the argument
 * @param <R> The type of the result
 */
public abstract class AbstractProcessor<A, R> implements Function<A, R> {

    private final R outputIfNoResult;
    private final BiConsumer<A, Exception> errorHandler;

    public AbstractProcessor(R outputIfNoResult, BiConsumer<A, Exception> errorHandler) {
        this.outputIfNoResult = outputIfNoResult;
        this.errorHandler = Objects.requireNonNull(errorHandler);
    }

    public abstract R process(A argument);
    
    @Override
    public R apply(A argument) {

        try{
            
            return this.process(argument);

        }catch(Exception exception) {

            this.errorHandler.accept(argument, exception);
            
            return outputIfNoResult;
        }
    }
}
