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

import feign.InvocationHandlerFactory;
import feign.Target;

import javax.ws.rs.BeanParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sskrla on 10/12/15.
 */
class BeanParamInvocationHandlerFactory implements InvocationHandlerFactory {
    final InvocationHandlerFactory delegate;
    final BeanParamTransformerFactory factory = new BeanParamTransformerFactory();

    public BeanParamInvocationHandlerFactory() {
        this.delegate = new InvocationHandlerFactory.Default();
    }

    public BeanParamInvocationHandlerFactory(InvocationHandlerFactory delegate) {
        this.delegate = delegate;
    }

    public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
        Map<Method, MethodHandler> overriddenDispatch = new HashMap<Method, MethodHandler>();
        for(Map.Entry<Method, MethodHandler> entry: dispatch.entrySet()) {
            int index = beanParamIndex(entry.getKey());
            if(index > -1) {
                overriddenDispatch.put(
                    entry.getKey(),
                    new BeanParamMethodHandler(
                        entry.getValue(),
                        factory.createTransformer(entry.getKey().getParameterTypes()[index], index),
                        index));
            } else {
                overriddenDispatch.put(entry.getKey(), entry.getValue());
            }
        }

        return delegate.create(target, overriddenDispatch);
    }

    int beanParamIndex(Method method) {
        Annotation[][] annotations =method.getParameterAnnotations();
        for(int i=0; i<annotations.length; i++) {
            for(Annotation annotation: annotations[i]) {
                if(BeanParam.class.isAssignableFrom(annotation.getClass()))
                    return i;
            }
        }

        return -1;
    }

    public static class BeanParamMethodHandler implements MethodHandler {
        final MethodHandler delegate;
        final BeanParamTransformer transformer;
        final int paramIndex;

        public BeanParamMethodHandler(MethodHandler delegate, BeanParamTransformer transformer, int paramIndex) {
            this.delegate = delegate;
            this.transformer = transformer;
            this.paramIndex = paramIndex;
        }

        public Object invoke(Object[] argv) throws Throwable {
            try {
                Map<String, Object> params = transformer.transform(argv);
                BeanParamEncoder.setContext(new BeanParamEncoder.EncoderContext(
                    params,
                    transformer.formParams(),
                    transformer.queryParams(),
                    transformer.headerParams()));
                return delegate.invoke(argv);

            } finally {
                BeanParamEncoder.clearContext();
            }
        }
    }
}
