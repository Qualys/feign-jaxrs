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

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import feign.template.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by sskrla on 10/12/15.
 */
class BeanParamEncoder implements Encoder {
    final Encoder delegate;

    public BeanParamEncoder() {
        this.delegate = new Encoder.Default();
    }

    public BeanParamEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (template.methodMetadata().indexToExpander() == null)
            template.methodMetadata().indexToExpander(new HashMap<>());

        if (object instanceof EncoderContext) {
            EncoderContext ctx = (EncoderContext) object;
            for (String name : ctx.transformer.queryParams()) {
                template.query(name, "{" + name + "}");
            }
            for (String name : ctx.transformer.headerParams()) {
                if (ctx.values.get(name) != null)
                    template.header(name, String.valueOf(ctx.values.get(name)));
            }

            resolve(template, ctx.values);
        } else {
            this.delegate.encode(object, bodyType, template);
        }
    }

    private static final Pattern ESCAPED_CURLY_BRACES = Pattern.compile("%7B(\\w+)%7D");

    private void resolve(RequestTemplate mutable, Map<String, Object> variables) {
        JaxrsUriTemplate uriTemplate = JaxrsUriTemplate.create(mutable.url(), !mutable.decodeSlash(),
                mutable.requestCharset());

        /// escape opening curly brace before expand
        variables.forEach((key, value) -> {
            if (value instanceof String) {
                String escapedValue = ((String) value).replace("{", "%7B");
                variables.put(key, escapedValue);
            }
        });

        String expanded = uriTemplate.expand(variables);

        /// unescape opening curly brace and params after expand
        if (expanded != null) {
            expanded = ESCAPED_CURLY_BRACES.matcher(expanded).replaceAll("{$1}").replace("%257B", "%7B");
        }

        mutable.uri(expanded);
    }
}
