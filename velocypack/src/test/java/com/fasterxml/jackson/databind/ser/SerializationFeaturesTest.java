package com.fasterxml.jackson.databind.ser;

import java.io.*;
import java.util.*;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.*;

/**
 * Unit tests for checking handling of some of {@link MapperFeature}s
 * and {@link SerializationFeature}s for serialization.
 */
public class SerializationFeaturesTest
    extends BaseMapTest
{
    static class CloseableBean implements Closeable
    {
        public int a = 3;

        protected boolean wasClosed = false;

        @Override
        public void close() throws IOException {
            wasClosed = true;
        }
    }

    private static class StringListBean {
        @SuppressWarnings("unused")
        public Collection<String> values;
        
        public StringListBean(Collection<String> v) { values = v; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    // Test for [JACKSON-282]
    @SuppressWarnings("resource")
    public void testCloseCloseable() throws IOException
    {
        ObjectMapper m = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        // default should be disabled:
        CloseableBean bean = new CloseableBean(); com.fasterxml.jackson.VPackUtils.toJson(
        m.writeValueAsBytes(bean));
        assertFalse(bean.wasClosed);

        // but can enable it:
        m.configure(SerializationFeature.CLOSE_CLOSEABLE, true);
        bean = new CloseableBean(); com.fasterxml.jackson.VPackUtils.toJson(
        m.writeValueAsBytes(bean));
        assertTrue(bean.wasClosed);

        // also: let's ensure that ObjectWriter won't interfere with it
        bean = new CloseableBean(); com.fasterxml.jackson.VPackUtils.toJson(
        m.writerFor(CloseableBean.class).writeValueAsBytes(bean));
        assertTrue(bean.wasClosed);
    }

    // Test for [JACKSON-289]
    public void testCharArrays() throws IOException
    {
        char[] chars = new char[] { 'a','b','c' };
        ObjectMapper m = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        // default: serialize as Strings
        assertEquals(quote("abc"), com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(chars)));
        
        // new feature: serialize as JSON array:
        m.configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true);
        assertEquals("[\"a\",\"b\",\"c\"]", com.fasterxml.jackson.VPackUtils.toJson( m.writeValueAsBytes(chars)));
    }

    // Test for [JACKSON-401]
    public void testFlushingAutomatic() throws IOException
    {
        ObjectMapper mapper = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        assertTrue(mapper.getSerializationConfig().isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE));
        // default is to flush after writeValue()
        StringWriter sw = new StringWriter();
        JsonGenerator g = mapper.getFactory().createGenerator(sw);
        mapper.writeValue(g, Integer.valueOf(13));
        assertEquals("13", sw.toString());
        g.close();

        // ditto with ObjectWriter
        sw = new StringWriter();
        g = mapper.getFactory().createGenerator(sw);
        ObjectWriter ow = mapper.writer();
        ow.writeValue(g, Integer.valueOf(99));
        assertEquals("99", sw.toString());
        g.close();
    }

    public void testFlushingNotAutomatic() throws IOException
    {
        // but should not occur if configured otherwise
        ObjectMapper mapper = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        mapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, false);
        StringWriter sw = new StringWriter();
        JsonGenerator g = mapper.getFactory().createGenerator(sw);

        mapper.writeValue(g, Integer.valueOf(13));
        // no flushing now:
        assertEquals("", sw.toString());
        // except when actually flushing
        g.flush();
        assertEquals("13", sw.toString());
        g.close();
        // Also, same should happen with ObjectWriter
        sw = new StringWriter();
        g = mapper.getFactory().createGenerator(sw);
        ObjectWriter ow = mapper.writer();
        ow.writeValue(g, Integer.valueOf(99));
        assertEquals("", sw.toString());
        // except when actually flushing
        g.flush();
        assertEquals("99", sw.toString());
        g.close();
    }

    public void testSingleElementCollections() throws IOException
    {
        final ObjectWriter writer = objectWriter().with(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);

        // Lists:
        ArrayList<String> strs = new ArrayList<String>();
        strs.add("xyz");
        assertEquals(quote("xyz"), com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(strs)));
        ArrayList<Integer> ints = new ArrayList<Integer>();
        ints.add(13);
        assertEquals("13", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(ints)));

        // other Collections, like Sets:
        HashSet<Long> longs = new HashSet<Long>();
        longs.add(42L);
        assertEquals("42", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(longs)));
        // [databind#180]
        final String EXP_STRINGS = "{\"values\":\"foo\"}";
        assertEquals(EXP_STRINGS, com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new StringListBean(Collections.singletonList("foo")))));

        final Set<String> SET = new HashSet<String>();
        SET.add("foo");
        assertEquals(EXP_STRINGS, com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new StringListBean(SET))));
        
        // arrays:
        assertEquals("true", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new boolean[] { true })));
        assertEquals("[true,false]", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new boolean[] { true, false })));
        assertEquals("true", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new Boolean[] { Boolean.TRUE })));

        assertEquals("3", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new short[] { 3 })));
        assertEquals("[3,2]", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new short[] { 3, 2 })));
        
        assertEquals("3", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new int[] { 3 })));
        assertEquals("[3,2]", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new int[] { 3, 2 })));

        assertEquals("1", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new long[] { 1L })));
        assertEquals("[-1,4]", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new long[] { -1L, 4L })));

        assertEquals("0.5", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new double[] { 0.5 })));
        assertEquals("[0.5,2.5]", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new double[] { 0.5, 2.5 })));

        assertEquals("0.5", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new float[] { 0.5f })));
        assertEquals("[0.5,2.5]", com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new float[] { 0.5f, 2.5f })));
        
        assertEquals(quote("foo"), com.fasterxml.jackson.VPackUtils.toJson( writer.writeValueAsBytes(new String[] { "foo" })));
    }
}
