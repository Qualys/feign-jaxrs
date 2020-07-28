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

import java.util.*;

/**
 * Created by agaifutdinov-inovus on 07/27/20.
 */
public class ResolvedUrlUtil {

    private ResolvedUrlUtil() {
    }

    /**
     * Adds unresolved expressions into RequestTemplate url after resolving
     *
     * @param originalTemplate original RequestTemplate
     * @param resolvedTemplate RequestTemplate after resolving
     * @return resolved template url with unresolved expressions
     */
    public static String getUnresolvedUrl(RequestTemplate originalTemplate, RequestTemplate resolvedTemplate) {
        StringBuilder result = new StringBuilder();

        String originalPath = originalTemplate.url().split("\\?", 2)[0];
        String resolvedPath = resolvedTemplate.url().split("\\?", 2)[0];
        String resolvedQuery = resolvedTemplate.url().contains("?") ?
                "?" + resolvedTemplate.url().split("\\?", 2)[1] :
                "";
        Map<String, Collection<String>> queries = new HashMap<>(originalTemplate.queries());
        resolvedTemplate.queries().keySet().forEach(queries::remove);

        result.append(getUnresolvedPath(originalPath, resolvedPath));

        StringBuilder unresolvedQuery = getUnresolvedQuery(queries);

        if (resolvedQuery.length() == 0 && unresolvedQuery.length() != 0) {
            unresolvedQuery = unresolvedQuery.replace(0, 1, "?");
            result.append(unresolvedQuery);
        } else {
            result.append(resolvedQuery)
                    .append(unresolvedQuery);
        }

        return result.toString();
    }

    private static StringBuilder getUnresolvedQuery(Map<String, Collection<String>> queries) {
        StringBuilder query = new StringBuilder();
        queries.forEach((key, values) -> values.forEach(value -> query.append("&").append(key).append("=").append(value)));

        return query;
    }

    private static StringBuilder getUnresolvedPath(String original, String resolved) {
        StringBuilder result = new StringBuilder();
        List<String> tokens = tokenize(original);
        int idx = 0;

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String nextToken = i + 1 < tokens.size() ?
                    tokens.get(i + 1) :
                    null;

            if (token.startsWith("{")) {
                if (nextToken == null) {
                    if (idx == resolved.length()) {
                        result.append(token);
                    } else {
                        result.append(resolved.substring(idx));
                    }
                } else if (resolved.indexOf(nextToken, idx) == idx) {
                    result.append(token);
                } else {
                    result.append(resolved, idx, resolved.indexOf(nextToken, idx));
                    idx = resolved.indexOf(nextToken, idx);
                }
            } else {
                result.append(token);
                idx += token.length();
            }
        }

        return result;
    }

    /* Taken from feign.template.Template.ChunkTokenizer */
    private static List<String> tokenize(String template) {
        List<String> tokens = new ArrayList<>();
        boolean outside = true;
        int level = 0;
        int lastIndex = 0;
        int idx;

        /* loop through the template, character by character */
        for (idx = 0; idx < template.length(); idx++) {
            if (template.charAt(idx) == '{') {
                /* start of an expression */
                if (outside) {
                    /* outside of an expression */
                    if (lastIndex < idx) {
                        /* this is the start of a new token */
                        tokens.add(template.substring(lastIndex, idx));
                    }
                    lastIndex = idx;

                    /*
                     * no longer outside of an expression, additional characters will be treated as in an
                     * expression
                     */
                    outside = false;
                } else {
                    /* nested braces, increase our nesting level */
                    level++;
                }
            } else if (template.charAt(idx) == '}' && !outside) {
                /* the end of an expression */
                if (level > 0) {
                    /*
                     * sometimes we see nested expressions, we only want the outer most expression
                     * boundaries.
                     */
                    level--;
                } else {
                    /* outermost boundary */
                    if (lastIndex < idx) {
                        /* this is the end of an expression token */
                        tokens.add(template.substring(lastIndex, idx + 1));
                    }
                    lastIndex = idx + 1;

                    /* outside an expression */
                    outside = true;
                }
            }
        }
        if (lastIndex < idx) {
            /* grab the remaining chunk */
            tokens.add(template.substring(lastIndex, idx));
        }

        return tokens;
    }
}