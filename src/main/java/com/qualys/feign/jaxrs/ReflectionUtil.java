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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sskrla on 10/7/15.
 */
final class ReflectionUtil {
    private ReflectionUtil() {
    }

    /**
     * Returns all declared field, including those on superclasses, optionally setting accessible set to true.
     *
     * @param cls class to analyze
     * @param accessible make fields accessible
     * @return all class fields
     */
    public static Field[] getAllDeclaredFields(Class<?> cls, boolean accessible) {
        List<Field> fields = new ArrayList<>();
        while (cls != null) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }

        if (accessible)
            fields.forEach(field -> field.setAccessible(true));

        return fields.toArray(new Field[0]);
    }
}
