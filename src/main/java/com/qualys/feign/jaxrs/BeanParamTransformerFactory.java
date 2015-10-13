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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Multimaps.invertFrom;
import static com.google.common.collect.Multimaps.transformValues;
import static java.lang.String.format;

/**
 * Created by sskrla on 10/13/15.
 */
public class BeanParamTransformerFactory {
    Multimap<String, Annotation> getNames(Annotation[] annotations) {
        ImmutableMultimap.Builder<String, Annotation> names = ImmutableMultimap.builder();
        for(Annotation annotation: annotations) {
            Class<?> cls = annotation.getClass();
            if(QueryParam.class.isAssignableFrom(cls))
                names.put(((QueryParam) annotation).value(), annotation);

            if(FormParam.class.isAssignableFrom(cls))
                names.put(((FormParam) annotation).value(), annotation);

            if(HeaderParam.class.isAssignableFrom(cls))
                names.put(((HeaderParam) annotation).value(), annotation);

            if(PathParam.class.isAssignableFrom(cls))
                names.put(((PathParam) annotation).value(), annotation);
        }

        return names.build();
    }

    protected BeanParamTransformer createTransformer(Type beanClass, int paramIndex) {
        try {
            List<BeanParamPropertyMetadata> propertyMetas = new ArrayList<BeanParamPropertyMetadata>();

            // Find annotated write methods and their respective reads
            Map<String, PropertyDescriptor> descriptorsByName = new HashMap<String, PropertyDescriptor>();
            BeanInfo info = Introspector.getBeanInfo((Class<?>) beanClass);
            for(PropertyDescriptor prop: info.getPropertyDescriptors()) {
                if(prop.getReadMethod() != null && prop.getWriteMethod() != null) {
                    Multimap<String, Annotation> names = getNames(prop.getWriteMethod().getAnnotations());
                    if(!names.isEmpty()) {
                        propertyMetas.add(new BeanParamPropertyMetadata(names, null, prop.getReadMethod()));
                    }
                }
                descriptorsByName.put(prop.getName(), prop);
            }

            // Find annotated fields, prefer getter access but use field in none is found
            for (Field field : ReflectionUtil.getAllDeclaredFields((Class<?>) beanClass, true)) {
                String fieldName = field.getName();
                if(descriptorsByName.containsKey(fieldName)) {
                    PropertyDescriptor descriptor = descriptorsByName.get(fieldName);
                    Multimap<String, Annotation> names = getNames(field.getAnnotations());
                    if(descriptor.getReadMethod() != null && !names.isEmpty()) {
                        propertyMetas.add(new BeanParamPropertyMetadata(names, null, descriptor.getReadMethod()));
                        continue;
                    }
                }

                Multimap<String, Annotation> names = getNames(field.getAnnotations());
                if(!names.isEmpty())
                    propertyMetas.add(new BeanParamPropertyMetadata(names, field, null));
            }

            String[][] names = new String[propertyMetas.size()][];
            Method[]   getters  = new Method[propertyMetas.size()];
            Field[]    fields = new Field[propertyMetas.size()];
            Multimap<Class<?>, String> params = ArrayListMultimap.create();
            for(int i=0; i<propertyMetas.size(); i++) {
                BeanParamPropertyMetadata propertyMetadata = propertyMetas.get(i);
                fields[i] = propertyMetadata.property;
                getters[i] = propertyMetadata.getter;
                names[i] = propertyMetadata.names.keySet().toArray(new String[]{});
                invertFrom(
                    transformValues(propertyMetadata.names, v -> (Class<?>) v.getClass().getInterfaces()[0]),
                    params);
            }
            return new BeanParamTransformer(names, ImmutableMultimap.copyOf(params), fields, getters, paramIndex);

        } catch(IntrospectionException e) {
            throw new RuntimeException(format("Unable to build bean info for %s", beanClass), e);
        }
    }

    static class BeanParamPropertyMetadata {
        final Multimap<String, Annotation> names;
        final Field  property;
        final Method getter;

        public BeanParamPropertyMetadata(
                Multimap<String, Annotation> names,
                Field property,
                Method getter) {

            this.names = names;
            this.property = property;
            this.getter = getter;
        }
    }
}
