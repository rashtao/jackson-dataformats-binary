package com.fasterxml.jackson.databind.misc;

import java.util.Arrays;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;

public class TestJSONP
    extends BaseMapTest
{
    static class Base {
        public String a;
    }
    static class Impl extends Base {
        public String b;

        public Impl(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    private final ObjectMapper MAPPER = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();

    public void testSimpleScalars() throws Exception
    {
        assertEquals("callback(\"abc\")", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new JSONPObject("callback", "abc"))));
        assertEquals("calc(123)", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new JSONPObject("calc", Integer.valueOf(123)))));
        assertEquals("dummy(null)", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new JSONPObject("dummy", null))));
    }

    public void testSimpleBean() throws Exception
    {
        assertEquals("xxx({\"a\":\"123\",\"b\":\"456\"})", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new JSONPObject("xxx",
                        new Impl("123", "456")))));
    }
    
    /**
     * Test to ensure that it is possible to force a static type for wrapped
     * value.
     */
    public void testWithType() throws Exception
    {
        Object ob = new Impl("abc", "def");
        JavaType type = MAPPER.constructType(Base.class);
        assertEquals("do({\"a\":\"abc\"})", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new JSONPObject("do", ob, type))));
    }

    public void testGeneralWrapping() throws Exception
    {
        JSONWrappedObject input = new JSONWrappedObject("/*Foo*/", "\n// the end",
                Arrays.asList());
        assertEquals("/*Foo*/[]\n// the end", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(input)));
    }
}
