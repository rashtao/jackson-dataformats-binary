package com.fasterxml.jackson.databind.ser.jdk;

import java.util.*;

import com.fasterxml.jackson.databind.*;

public class KeySerializers1679Test extends BaseMapTest
{
    /*
    /**********************************************************
    /* Unit tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();

    // [databind#1679]
    public void testRecursion1679() throws Exception
    {
        Map<Object, Object> objectMap = new HashMap<Object, Object>();
        objectMap.put(new Object(), "foo");
        String json = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(objectMap));
        assertNotNull(json);
    }
}
