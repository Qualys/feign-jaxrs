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

import javax.ws.rs.*;

/**
 * Created by sskrla on 10/12/15.
 */
public interface QueryResource {
    @GET String withParam(@BeanParam QueryParamBean bean);
    @GET @Path("headers") String withHeader(@BeanParam HeaderBeanParam bean);
    @GET @Path("{id}") String withPath(@BeanParam PathBeanParam path);

    class QueryParamBean {
        @QueryParam("one") String param1;
        @QueryParam("two") String param2;

        public String getParam1() {
            return param1;
        }

        public void setParam1(String param1) {
            this.param1 = param1;
        }

        public String getParam2() {
            return param2;
        }

        public void setParam2(String param2) {
            this.param2 = param2;
        }
    }

    class HeaderBeanParam {
        @HeaderParam("test") String testParam;

        public String getTestParam() {
            return testParam;
        }

        public void setTestParam(String testParam) {
            this.testParam = testParam;
        }
    }

    class PathBeanParam {
        @PathParam("id") int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
