/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.microsphere.microprofile.faulttolerance;

import io.microsphere.enterprise.interceptor.AnnotatedInterceptor;
import io.microsphere.lang.function.ThrowableSupplier;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceDefinitionException;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import static io.microsphere.microprofile.faulttolerance.AsynchronousInterceptor.ASYNCHRONOUS_PRIORITY;
import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * The interceptor implementation for the annotation {@link Asynchronous} of
 * MicroProfile Fault Tolerance
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Asynchronous
@Interceptor
@Priority(ASYNCHRONOUS_PRIORITY)
public class AsynchronousInterceptor extends AnnotatedInterceptor<Asynchronous> {

    public static final int ASYNCHRONOUS_PRIORITY = TimeoutInterceptor.TIMEOUT_PRIORITY + 100;

    // TODO ExecutorService fixed size = external Server Thread numbers
    private final ExecutorService executor = ForkJoinPool.commonPool();

    public AsynchronousInterceptor() {
        super();
    }

    @Override
    protected Object intercept(InvocationContext context, Asynchronous bindingAnnotation) throws Throwable {
        Method method = context.getMethod();
        if (validateMethod(method, Future.class)) {
            return executeFuture(context);
        } else if (validateMethod(method, CompletionStage.class)) {
            return executeCompletableFuture(context);
        } else {
            throw new FaultToleranceDefinitionException("The return type of @Asynchronous method must be " +
                    "java.util.concurrent.Future or java.util.concurrent.CompletableFuture!");
        }
    }

    private boolean validateMethod(Method method, Class<?> expectedReturnType)
            throws FaultToleranceDefinitionException {
        Class<?> returnType = method.getReturnType();
        return expectedReturnType.isAssignableFrom(returnType);
    }

    private Future<?> executeFuture(InvocationContext context) {
        return executor.submit(context::proceed);
    }

    private CompletableFuture<?> executeCompletableFuture(InvocationContext context) {
        return supplyAsync(() -> ThrowableSupplier.execute(context::proceed), executor);
    }
}
