package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;

/**
 * Unit tests for verifying that field-backed properties can also be
 * deserialized as well as setter-accessible properties.
 */
public class TestFieldDeserialization
    extends BaseMapTest
{
    /*
    /**********************************************************
    /* Annotated helper classes
    /**********************************************************
     */

    static class SimpleFieldBean
    {
        public int x, y;

        // not auto-detectable, not public
        int z;

        // ignored, not detectable either
        @JsonIgnore public int a;
    }

    static class SimpleFieldBean2
    {
        @JsonDeserialize String[] values;
    }

    @JsonAutoDetect(fieldVisibility=Visibility.NONE)
    static class NoAutoDetectBean
    {
        // not auto-detectable any more
        public int z;

        @JsonProperty("z")
        public int _z;
    }

    // Let's test invalid bean too
    static class DupFieldBean
    {
        public int z;

        @JsonProperty("z")
        public int _z;
    }

    public static class DupFieldBean2
    {
        @JsonProperty("foo")
        public int _z;

        @JsonDeserialize
        private int foo;
    }

    public static class OkDupFieldBean
        extends SimpleFieldBean
    {
        @JsonProperty("x")
        protected int myX = 10;

        @SuppressWarnings("hiding")
        public int y = 11;
    }
    
    abstract static class Abstract { }
    static class Concrete extends Abstract
    {
        String value;

        public Concrete(String v) { value = v; }
    }

    static class AbstractWrapper {
        @JsonDeserialize(as=Concrete.class)
        public Abstract value;
    }

    /*
    /**********************************************************
    /* Main tests
    /**********************************************************
     */

    public void testSimpleAutoDetect() throws Exception
    {
        ObjectMapper m = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        SimpleFieldBean result = m.readValue("{ \"x\" : -13 }",
                                           SimpleFieldBean.class);
        assertEquals(-13, result.x);
        assertEquals(0, result.y);
    }

    public void testSimpleAnnotation() throws Exception
    {
        ObjectMapper m = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        SimpleFieldBean2 bean = m.readValue("{ \"values\" : [ \"x\", \"y\" ] }",
                SimpleFieldBean2.class);
        String[] values = bean.values;
        assertNotNull(values);
        assertEquals(2, values.length);
        assertEquals("x", values[0]);
        assertEquals("y", values[1]);
    }

    public void testNoAutoDetect() throws Exception
    {
        ObjectMapper m = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        NoAutoDetectBean bean = m.readValue("{ \"z\" : 7 }",
                                            NoAutoDetectBean.class);
        assertEquals(7, bean._z);
    }

    public void testTypeAnnotation() throws Exception
    {
        ObjectMapper m = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        AbstractWrapper w = m.readValue("{ \"value\" : \"abc\" }",
                                        AbstractWrapper.class);
        Abstract bean = w.value;
        assertNotNull(bean);
        assertEquals(Concrete.class, bean.getClass());
        assertEquals("abc", ((Concrete)bean).value);
    }

    public void testFailureDueToDups() throws Exception
    {
        try {
            writeAndMap(new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper(), new DupFieldBean());
        } catch (JsonMappingException e) {
            verifyException(e, "Multiple fields representing property");
        }
    }

    public void testFailureDueToDups2() throws Exception
    {
        try {
            writeAndMap(new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper(), new DupFieldBean2());
        } catch (JsonMappingException e) {
            verifyException(e, "Multiple fields representing property");
        }
    }

    // For [JACKSON-226], acceptable field overrides
    public void testOkFieldOverride() throws Exception
    {
        ObjectMapper m = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        OkDupFieldBean result = m.readValue("{ \"x\" : 1, \"y\" : 2 }",
                OkDupFieldBean.class);
        assertEquals(1, result.myX);
        assertEquals(2, result.y);
    }
}
