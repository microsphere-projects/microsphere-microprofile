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
package io.microsphere.microprofile.config.source.servlet;

import io.microsphere.microprofile.config.source.EnumerableConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.servlet.FilterConfig;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

/**
 * The {@link ConfigSource} implementation based on {@link FilterConfig}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class FilterConfigSource extends EnumerableConfigSource {

    private final FilterConfig filterConfig;

    public FilterConfigSource(FilterConfig filterConfig) {
        super(format("Filter[name:%s] Init Parameters", filterConfig.getFilterName()), 550);
        this.filterConfig = filterConfig;
    }

    @Override
    protected Supplier<Enumeration<String>> namesSupplier() {
        return filterConfig::getInitParameterNames;
    }

    @Override
    protected Function<String, String> valueResolver() {
        return filterConfig::getInitParameter;
    }
}
