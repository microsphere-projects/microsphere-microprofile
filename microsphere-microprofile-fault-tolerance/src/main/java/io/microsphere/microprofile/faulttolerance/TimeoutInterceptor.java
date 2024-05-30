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
import org.eclipse.microprofile.faulttolerance.Timeout;

import javax.annotation.Priority;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.microsphere.microprofile.faulttolerance.TimeoutInterceptor.TIMEOUT_PRIORITY;
import static io.microsphere.microprofile.faulttolerance.util.TimeUtils.toTimeUnit;

/**
 * The interceptor implementation for the annotation {@link Timeout} of
 * MicroProfile Fault Tolerance
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Timeout
@Interceptor
@Priority(TIMEOUT_PRIORITY)
public class TimeoutInterceptor extends AnnotatedInterceptor<Timeout> {

    public static final int TIMEOUT_PRIORITY = RetryInterceptor.RETRY_PRIORITY + 100;

    // TODO ExecutorService fixed size = external Server Thread numbers
    private final ExecutorService executor = ForkJoinPool.commonPool();

    public TimeoutInterceptor() {
        super();
    }

    @Override
    protected Object intercept(InvocationContext context, Timeout timeout) throws Exception {
        ChronoUnit chronoUnit = timeout.unit();
        long timeValue = timeout.value();
        TimeUnit timeUnit = toTimeUnit(chronoUnit);

        Future future = executor.submit(context::proceed);

        try {
            return future.get(timeValue, timeUnit);
        } catch (TimeoutException e) {
            throw new org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException(e);
        }
    }

}
