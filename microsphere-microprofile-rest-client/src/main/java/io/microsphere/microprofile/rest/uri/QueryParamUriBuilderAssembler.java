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
package io.microsphere.microprofile.rest.uri;

import io.microsphere.microprofile.rest.annotation.AnnotatedParamMetadata;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

/**
 * {@link QueryParam} {@link UriBuilderAssembler}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class QueryParamUriBuilderAssembler implements UriBuilderAssembler<QueryParam> {

    @Override
    public void assemble(UriBuilder uriBuilder, AnnotatedParamMetadata metadata, Object[] args) {
        String paramName = metadata.getParamName();
        int paramIndex = metadata.getParameterIndex();
        Object paramValue = args[paramIndex];
        uriBuilder.queryParam(paramName, paramValue);
    }
}
