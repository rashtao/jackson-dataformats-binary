package com.fasterxml.jackson.databind.seq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.ext.ExternalTypeIdWithCreatorTest;

import java.util.Iterator;

/**
 * Tests to verify aspects of error recover for reading using
 * iterator.
 */
public class ReadRecoveryTest extends BaseMapTest
{
    static class Bean {
        public int a, b;

        @Override public String toString() { return "{Bean, a="+a+", b="+b+"}"; }
    }

}
