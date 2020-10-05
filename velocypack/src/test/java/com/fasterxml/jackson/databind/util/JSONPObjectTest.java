package com.fasterxml.jackson.databind.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.BaseMapTest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONPObjectTest extends BaseMapTest {

  private final String CALLBACK = "callback";
  private final ObjectMapper MAPPER = new com.fasterxml.jackson.dataformat.velocypack.VelocypackMapper();

  /**
   * Unit tests for checking that JSONP breaking characters U+2028 and U+2029 are escaped when creating a {@link JSONPObject}.
   */

  public void testU2028Escaped() throws IOException {
    String containsU2028 = String.format("This string contains %c char", '\u2028');
    JSONPObject jsonpObject = new JSONPObject(CALLBACK, containsU2028);
    String valueAsString = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(jsonpObject));
    assertFalse(valueAsString.contains("\u2028"));
  }

  public void testU2029Escaped() throws IOException {
    String containsU2029 = String.format("This string contains %c char", '\u2029');
    JSONPObject jsonpObject = new JSONPObject(CALLBACK, containsU2029);
    String valueAsString = com.fasterxml.jackson.VPackUtils.toJson( MAPPER.writeValueAsBytes(jsonpObject));
    assertFalse(valueAsString.contains("\u2029"));
  }

}
