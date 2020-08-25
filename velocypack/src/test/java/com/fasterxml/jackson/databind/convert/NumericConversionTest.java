package com.fasterxml.jackson.databind.convert;

import com.arangodb.velocypack.exception.VPackException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import static com.fasterxml.jackson.VPackUtils.toBytes;

public class NumericConversionTest extends BaseMapTest
{
    private final ObjectMapper MAPPER = sharedMapper();
    private final ObjectReader R = MAPPER.reader().without(DeserializationFeature.ACCEPT_FLOAT_AS_INT);

    public void testDoubleToInt() throws Exception
    {
        // by default, should be ok
        Integer I = MAPPER.readValue(toBytes("1.25"), Integer.class);
        assertEquals(1, I.intValue());
        IntWrapper w = MAPPER.readValue(toBytes("{\"i\":-2.25}"), IntWrapper.class);
        assertEquals(-2, w.i);
        int[] arr = MAPPER.readValue(toBytes("[1.25]"), int[].class);
        assertEquals(1, arr[0]);

        try {
            R.forType(Integer.class).readValue(toBytes("1.5"));
            fail("Should not pass");
        } catch (Exception e) {
            assertTrue(e instanceof VPackException);
        }
        try {
            R.forType(Integer.TYPE).readValue(toBytes("1.5"));
            fail("Should not pass");
        } catch (Exception e) {
            assertTrue(e instanceof VPackException);
        }
        try {
            R.forType(IntWrapper.class).readValue(toBytes("{\"i\":-2.25 }"));
            fail("Should not pass");
        } catch (Exception e) {
            assertTrue(e instanceof JsonMappingException);
        }
        try {
            R.forType(int[].class).readValue(toBytes("[ 2.5 ]"));
            fail("Should not pass");
        } catch (Exception e) {
            assertTrue(e instanceof JsonMappingException);
        }
    }

    public void testDoubleToLong() throws Exception
    {
        // by default, should be ok
        Long L = MAPPER.readValue(toBytes(" 3.33 "), Long.class);
        assertEquals(3L, L.longValue());
        LongWrapper w = MAPPER.readValue(toBytes("{\"l\":-2.25 }"), LongWrapper.class);
        assertEquals(-2L, w.l);
        long[] arr = MAPPER.readValue(toBytes("[ 1.25 ]"), long[].class);
        assertEquals(1, arr[0]);

        try {
            R.forType(Long.class).readValue(toBytes("1.5"));
            fail("Should not pass");
        } catch (Exception e) {
            assertTrue(e instanceof VPackException);
        }

        try {
            R.forType(Long.TYPE).readValue(toBytes("1.5"));
            fail("Should not pass");
        } catch (Exception e) {
            assertTrue(e instanceof VPackException);
        }
        
        try {
            R.forType(LongWrapper.class).readValue(toBytes("{\"l\": 7.7 }"));
            fail("Should not pass");
        } catch (Exception e) {
            assertTrue(e instanceof JsonMappingException);
        }
        try {
            R.forType(long[].class).readValue(toBytes("[ 2.5 ]"));
            fail("Should not pass");
        } catch (Exception e) {
            assertTrue(e instanceof JsonMappingException);
        }
    }
}
