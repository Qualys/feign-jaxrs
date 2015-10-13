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

import com.google.common.collect.Multimap;
import feign.Param;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sskrla on 10/7/15.
 */
public class BeanParamTransformer implements Param.Expander {
    final String[][] names;
    final Multimap<Class<?>, String> params;
    final Field[] fields;
    final Method[] getters;
    final int index;

    public BeanParamTransformer(
            String[][] names,
            Multimap<Class<?>, String> params,
            Field[] fields,
            Method[] getters,
            int index) {

        this.names = names;
        this.params = params;
        this.fields = fields;
        this.getters = getters;
        this.index = index;
    }

    public Map<String, Object> transform(Object[] argv) {
        Object param = argv[index];
        try {
            Map<String, Object> mapped = new HashMap<String, Object>();
            for (int i = 0; i < names.length; i++) {
                Object value = fields[i] != null ? fields[i].get(param) : getters[i].invoke(param);
                for (String name : names[i])
                    mapped.put(name, value);
            }

            return mapped;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<String> formParams() {
        return params.get(FormParam.class);
    }

    public Collection<String> queryParams() {
        return params.get(QueryParam.class);
    }

    public Collection<String> headerParams() {
        return params.get(HeaderParam.class);
    }



    public String expand(Object value) {
        throw new IllegalStateException("Not implemented");
    }
}
