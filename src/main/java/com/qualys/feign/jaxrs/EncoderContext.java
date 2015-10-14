/*
 * Licensed to Qualys, Inc. (QUALYS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * QUALYS licenses this file to You under the Apache License, Version 2.0
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
 *
 */

package com.qualys.feign.jaxrs;

import java.util.Collection;
import java.util.Map;

/**
 * Created by sskrla on 10/13/15.
 */
public class EncoderContext {
    final Map<String, Object> values;
    final Collection<String> formParams;
    final Collection<String> queryParams;
    final Collection<String> headerParams;

    EncoderContext(
            Map<String, Object> values,
            Collection<String> formParams,
            Collection<String> queryParams,
            Collection<String> headerParams) {

        this.values = values;
        this.formParams = formParams;
        this.queryParams = queryParams;
        this.headerParams = headerParams;
    }
}
