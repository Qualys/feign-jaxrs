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

import feign.Feign;
import feign.InvocationHandlerFactory;
import feign.codec.Encoder;
import feign.jaxrs.JAXRSContract;

/**
 * Created by sskrla on 10/13/15.
 */
public class JAXRS2Profile extends Feign.Builder {
    JAXRS2Profile() {
        encoder(new Encoder.Default());
        invocationHandlerFactory(new InvocationHandlerFactory.Default());
        contract(new JAXRSContract());
    }

    @Override
    public JAXRS2Profile encoder(Encoder encoder) {
        super.encoder(new BeanParamEncoder(encoder));
        return this;
    }

    @Override
    public JAXRS2Profile invocationHandlerFactory(InvocationHandlerFactory factory) {
        super.invocationHandlerFactory(new BeanParamInvocationHandlerFactory(factory));
        return this;
    }

    public static JAXRS2Profile create() {
        return new JAXRS2Profile();
    }
}
