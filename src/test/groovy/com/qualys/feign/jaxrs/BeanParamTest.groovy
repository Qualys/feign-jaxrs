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

package com.qualys.feign.jaxrs
import feign.Client
import feign.Feign
import feign.Request
import feign.Response
import feign.jaxrs2.JAXRS2Contract
import spock.lang.Specification
/**
 * Created by sskrla on 10/12/15.
 */
class BeanParamTest extends Specification {
    Request sent
    def client = Feign.builder()
        .encoder(new BeanParamEncoder())
        .invocationHandlerFactory(new BeanParamInvocationHandlerFactory())
        .contract(new JAXRS2Contract())
        .client(new Client() {
            @Override
            Response execute(Request request, Request.Options options) throws IOException {
                sent = request
                Response.builder().request(request).status(200).reason("OK").headers([:]).body(new byte[0]).build()
            }
        })
        .target(QueryResource, "http://localhost")

    def "query params"() {
        when:
        client.withParam(new QueryResource.QueryParamBean(param1: "one", param2: "two"))

        then:
        sent.url().contains("one=one")
        sent.url().contains("two=two")
    }

    def "null param not sent"() {
        when:
        client.withParam(new QueryResource.QueryParamBean(param1: "one"))

        then:
        sent.url().contains("one=one")
        !sent.url().contains("two=two")
    }

    def "header param"() {
        when:
        client.withHeader(new QueryResource.HeaderBeanParam(testParam: "ing"))

        then:
        sent.headers().get("test")[0] == "ing"
    }

    def "path param"() {
        when:
        client.withPath(new QueryResource.PathBeanParam(id: 42))

        then:
        sent.url() == "http://localhost/42"
    }
}
