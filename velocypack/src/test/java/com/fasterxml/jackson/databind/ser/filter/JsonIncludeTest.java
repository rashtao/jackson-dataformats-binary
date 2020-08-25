package com.fasterxml.jackson.databind.ser.filter;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Unit tests for checking that alternative settings for
 * {@link JsonSerialize#include} annotation property work
 * as expected.
 */
public class JsonIncludeTest
    extends BaseMapTest
{
    static class SimpleBean
    {
        public String getA() { return "a"; }
        public String getB() { return null; }
    }
    
    @JsonInclude(JsonInclude.Include.ALWAYS) // just to ensure default
    static class NoNullsBean
    {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String getA() { return null; }

        public String getB() { return null; }
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class NonDefaultBean
    {
        String _a = "a", _b = "b";

        NonDefaultBean() { }

        public String getA() { return _a; }
        public String getB() { return _b; }
    }

    // [databind#998]: Do not require no-arg constructor; but if not, defaults check
    //    has weaker interpretation
    @JsonPropertyOrder({ "x", "y", "z" })
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class NonDefaultBeanXYZ
    {
        public int x;
        public int y = 3;
        public int z = 7;

        NonDefaultBeanXYZ(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class MixedBean
    {
        String _a = "a", _b = "b";

        MixedBean() { }

        public String getA() { return _a; }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String getB() { return _b; }
    }

    // to ensure that default values work for collections as well
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class ListBean {
        public List<String> strings = new ArrayList<String>();
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static class ArrayBean {
        public int[] ints = new int[] { 1, 2 };
    }

    // Test to ensure that default exclusion works for fields too
    @JsonPropertyOrder({ "i1", "i2" })
    static class DefaultIntBean {
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public int i1;

        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public Integer i2;

        public DefaultIntBean(int i1, Integer i2) {
            this.i1 = i1;
            this.i2 = i2;
        }
    }

    static class NonEmptyString {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public String value;

        public NonEmptyString(String v) { value = v; }
    }

    static class NonEmptyInt {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public int value;

        public NonEmptyInt(int v) { value = v; }
    }

    static class NonEmptyDouble {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public double value;

        public NonEmptyDouble(double v) { value = v; }
    }

    static class NonEmpty<T> {
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public T value;

        public NonEmpty(T v) { value = v; }
    }

    static class NonEmptyDate extends NonEmpty<Date> {
        public NonEmptyDate(Date v) { super(v); }
    }
    static class NonEmptyCalendar extends NonEmpty<Calendar> {
        public NonEmptyCalendar(Calendar v) { super(v); }
    }

    static class NonDefault<T> {
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public T value;

        public NonDefault(T v) { value = v; }
    }

    static class NonDefaultDate extends NonDefault<Date> {
        public NonDefaultDate(Date v) { super(v); }
    }
    static class NonDefaultCalendar extends NonDefault<Calendar> {
        public NonDefaultCalendar(Calendar v) { super(v); }
    }

    // [databind#1351]

    static class Issue1351Bean
    {
        public final String first;
        public final double second;

        public Issue1351Bean(String first, double second) {
            this.first = first;
            this.second = second;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    static abstract class Issue1351NonBeanParent
    {
        protected final int num;

        protected Issue1351NonBeanParent(int num) {
            this.num = num;
        }

        @JsonProperty("num")
        public int getNum() {
            return num;
        }
    }

    static class Issue1351NonBean extends Issue1351NonBeanParent {
        private String str;

        @JsonCreator
        public Issue1351NonBean(@JsonProperty("num") int num) {
            super(num);
        }

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    final private ObjectMapper MAPPER = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();

    public void testGlobal() throws IOException
    {
        Map<String,Object> result = writeAndMap(MAPPER, new SimpleBean());
        assertEquals(2, result.size());
        assertEquals("a", result.get("a"));
        assertNull(result.get("b"));
        assertTrue(result.containsKey("b"));
    }

    public void testNonNullByClass() throws IOException
    {
        Map<String,Object> result = writeAndMap(MAPPER, new NoNullsBean());
        assertEquals(1, result.size());
        assertFalse(result.containsKey("a"));
        assertNull(result.get("a"));
        assertTrue(result.containsKey("b"));
        assertNull(result.get("b"));
    }

    public void testNonDefaultByClass() throws IOException
    {
        NonDefaultBean bean = new NonDefaultBean();
        // need to change one of defaults
        bean._a = "notA";
        Map<String,Object> result = writeAndMap(MAPPER, bean);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("a"));
        assertEquals("notA", result.get("a"));
        assertFalse(result.containsKey("b"));
        assertNull(result.get("b"));
    }

    // [databind#998]
    public void testNonDefaultByClassNoCtor() throws IOException
    {
        NonDefaultBeanXYZ bean = new NonDefaultBeanXYZ(1, 2, 0);
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(bean));
        assertEquals(aposToQuotes("{'x':1,'y':2}"), json);
    }
    
    public void testMixedMethod() throws IOException
    {
        MixedBean bean = new MixedBean();
        bean._a = "xyz";
        bean._b = null;
        Map<String,Object> result = writeAndMap(MAPPER, bean);
        assertEquals(1, result.size());
        assertEquals("xyz", result.get("a"));
        assertFalse(result.containsKey("b"));

        bean._a = "a";
        bean._b = "b";
        result = writeAndMap(MAPPER, bean);
        assertEquals(1, result.size());
        assertEquals("b", result.get("b"));
        assertFalse(result.containsKey("a"));
    }

    public void testDefaultForEmptyList() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new ListBean())));
    }

    // NON_DEFAULT shoud work for arrays too
    public void testNonEmptyDefaultArray() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new ArrayBean())));
    }

    public void testDefaultForIntegers() throws IOException
    {
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new DefaultIntBean(0, Integer.valueOf(0)))));
        assertEquals("{\"i2\":1}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new DefaultIntBean(0, Integer.valueOf(1)))));
        assertEquals("{\"i1\":3}", com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(new DefaultIntBean(3, Integer.valueOf(0)))));
    }

    public void testEmptyInclusionScalars() throws IOException
    {
        ObjectMapper defMapper = MAPPER;
        ObjectMapper inclMapper = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        // First, Strings
        StringWrapper str = new StringWrapper("");
        assertEquals("{\"str\":\"\"}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(str)));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( inclMapper.writeValueAsBytes(str)));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( inclMapper.writeValueAsBytes(new StringWrapper())));

        assertEquals("{\"value\":\"x\"}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(new NonEmptyString("x"))));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(new NonEmptyString(""))));

        // Then numbers
        // 11-Nov-2015, tatu: As of Jackson 2.7, scalars should NOT be considered empty,
        //   except for wrappers if they are `null`
        assertEquals("{\"value\":12}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(new NonEmptyInt(12))));
        assertEquals("{\"value\":0}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(new NonEmptyInt(0))));

        assertEquals("{\"value\":1.25}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(new NonEmptyDouble(1.25))));
        assertEquals("{\"value\":0.0}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(new NonEmptyDouble(0.0))));

        IntWrapper zero = new IntWrapper(0);
        assertEquals("{\"i\":0}", com.fasterxml.jackson.VPackUtils.toJson( defMapper.writeValueAsBytes(zero)));
        assertEquals("{\"i\":0}", com.fasterxml.jackson.VPackUtils.toJson( inclMapper.writeValueAsBytes(zero)));
    }

    // [databind#1351], [databind#1417]
    public void testIssue1351() throws Exception
    {
        ObjectMapper mapper = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        assertEquals(aposToQuotes("{}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new Issue1351Bean(null, (double) 0))));
        // [databind#1417]
        assertEquals(aposToQuotes("{}"), com.fasterxml.jackson.VPackUtils.toJson(
                mapper.writeValueAsBytes(new Issue1351NonBean(0))));
    }

    // [databind#1550]
    public void testInclusionOfDate() throws Exception
    {
        final Date input = new Date(0L);
        assertEquals(aposToQuotes("{'value':0}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new NonEmptyDate(input))));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new NonDefaultDate(input))));

    
    }

    // [databind#1550]
    public void testInclusionOfCalendar() throws Exception
    {
        final Calendar input = new GregorianCalendar();
        input.setTimeInMillis(0L);
        assertEquals(aposToQuotes("{'value':0}"), com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new NonEmptyCalendar(input))));
        assertEquals("{}", com.fasterxml.jackson.VPackUtils.toJson(
                MAPPER.writeValueAsBytes(new NonDefaultCalendar(input))));
    }
}
