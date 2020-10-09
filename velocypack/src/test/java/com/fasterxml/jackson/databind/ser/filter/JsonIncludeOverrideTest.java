package com.fasterxml.jackson.databind.ser.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.velocypack.TestVelocypackMapper;

/**
 * Unit tests for checking that overridden settings for
 * {@link com.fasterxml.jackson.databind.annotation.JsonSerialize#include} annotation property work
 * as expected.
 */
public class JsonIncludeOverrideTest
    extends BaseMapTest
{
    @JsonPropertyOrder({"list", "map"})
    static class EmptyListMapBean
    {
        public List<String> list = Collections.emptyList();

        public Map<String,String> map = Collections.emptyMap();
    }

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonPropertyOrder({"num", "annotated", "plain"})
    static class MixedTypeAlwaysBean
    {
        @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
        public Integer num = null;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String annotated = null;

        public String plain = null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({"num", "annotated", "plain"})
    static class MixedTypeNonNullBean
    {
        @JsonInclude(JsonInclude.Include.USE_DEFAULTS)
        public Integer num = null;

        @JsonInclude(JsonInclude.Include.ALWAYS)
        public String annotated = null;

        public String plain = null;
    }

    public void testPropConfigOverridesForInclude() throws IOException
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        // First, with defaults, both included:
        JsonIncludeOverrideTest.EmptyListMapBean empty = new JsonIncludeOverrideTest.EmptyListMapBean();
        assertEquals(aposToQuotes("{'list':[],'map':{}}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(empty)));

        // and then change inclusion criteria for either
        mapper = new TestVelocypackMapper();
        mapper.configOverride(Map.class)
            .setInclude(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, null));
        assertEquals(aposToQuotes("{'list':[]}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(empty)));

        mapper = new TestVelocypackMapper();
        mapper.configOverride(List.class)
            .setInclude(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, null));
        assertEquals(aposToQuotes("{'map':{}}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(empty)));
    }

    public void testOverrideForIncludeAsPropertyNonNull() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        // First, with defaults, all but NON_NULL annotated included
        JsonIncludeOverrideTest.MixedTypeAlwaysBean nullValues = new JsonIncludeOverrideTest.MixedTypeAlwaysBean();
        assertEquals(aposToQuotes("{'num':null,'plain':null}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));

        // and then change inclusion as property criteria for either
        mapper = new TestVelocypackMapper();
        mapper.configOverride(String.class)
                .setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null));
        assertEquals("{\"num\":null}", com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));

        mapper = new TestVelocypackMapper();
        mapper.configOverride(Integer.class)
                .setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null));
        assertEquals("{\"plain\":null}", com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));
    }

    public void testOverrideForIncludeAsPropertyAlways() throws Exception
    {
        ObjectMapper mapper = new TestVelocypackMapper();
        // First, with defaults, only ALWAYS annotated included
        JsonIncludeOverrideTest.MixedTypeNonNullBean nullValues = new JsonIncludeOverrideTest.MixedTypeNonNullBean();
        assertEquals("{\"annotated\":null}", com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));

        // and then change inclusion as property criteria for either
        mapper = new TestVelocypackMapper();
        mapper.configOverride(String.class)
                .setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null));
        assertEquals(aposToQuotes("{'annotated':null,'plain':null}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));

        mapper = new TestVelocypackMapper();
        mapper.configOverride(Integer.class)
                .setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null));
        assertEquals(aposToQuotes("{'num':null,'annotated':null}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));
    }

    public void testOverridesForIncludeAndIncludeAsPropertyNonNull() throws Exception
    {
        // First, with ALWAYS override on containing bean, all included
        JsonIncludeOverrideTest.MixedTypeNonNullBean nullValues = new JsonIncludeOverrideTest.MixedTypeNonNullBean();
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.configOverride(JsonIncludeOverrideTest.MixedTypeNonNullBean.class)
                .setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null));
        assertEquals(aposToQuotes("{'num':null,'annotated':null,'plain':null}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));

        // and then change inclusion as property criteria for either
        mapper = new TestVelocypackMapper();
        mapper.configOverride(JsonIncludeOverrideTest.MixedTypeNonNullBean.class)
                .setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null));
        mapper.configOverride(String.class)
                .setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null));
        assertEquals(aposToQuotes("{'num':null,'annotated':null}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));

        mapper = new TestVelocypackMapper();
        mapper.configOverride(JsonIncludeOverrideTest.MixedTypeNonNullBean.class)
                .setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null));
        mapper.configOverride(Integer.class)
                .setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null));
        assertEquals(aposToQuotes("{'annotated':null,'plain':null}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));
    }

    public void testOverridesForIncludeAndIncludeAsPropertyAlways() throws Exception
    {
        // First, with NON_NULL override on containing bean, empty
        JsonIncludeOverrideTest.MixedTypeAlwaysBean nullValues = new JsonIncludeOverrideTest.MixedTypeAlwaysBean();
        ObjectMapper mapper = new TestVelocypackMapper();
        mapper.configOverride(JsonIncludeOverrideTest.MixedTypeAlwaysBean.class)
                .setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));

        // and then change inclusion as property criteria for either
        mapper = new TestVelocypackMapper();
        mapper.configOverride(JsonIncludeOverrideTest.MixedTypeAlwaysBean.class)
                .setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null));
        mapper.configOverride(String.class)
                .setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null));
        assertEquals("{\"plain\":null}", com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));

        mapper = new TestVelocypackMapper();
        mapper.configOverride(JsonIncludeOverrideTest.MixedTypeAlwaysBean.class)
                .setInclude(JsonInclude.Value
                        .construct(JsonInclude.Include.NON_NULL, null));
        mapper.configOverride(Integer.class)
                .setIncludeAsProperty(JsonInclude.Value
                        .construct(JsonInclude.Include.ALWAYS, null));
        assertEquals("{\"num\":null}", com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(nullValues)));
    }
}
