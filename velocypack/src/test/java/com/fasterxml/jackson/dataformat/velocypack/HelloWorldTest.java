package com.fasterxml.jackson.dataformat.velocypack;

import com.arangodb.velocypack.VPackSlice;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Michele Rastelli
 */
public class HelloWorldTest {

    static class Person {

        @JsonProperty("Vorname")
        private final String name;

        @JsonCreator
        public Person(String name) {
            this.name = name;
        }

    }

    @Test
    public void test() throws JsonProcessingException {
        Person p = new Person("mike");

        ObjectMapper mapper = new ObjectMapper(new VelocypackFactory());
        final ObjectWriter writer = mapper.writer();
        byte[] bytes = writer.writeValueAsBytes(p);
        VPackSlice slice = new VPackSlice(bytes);

        assertThat(slice.isObject(), is(true));
        Map.Entry<String, VPackSlice> e = slice.objectIterator().next();
        assertThat(e.getKey(), is("Vorname"));
        assertThat(e.getValue().isString(), is(true));
        assertThat(e.getValue().getAsString(), is("mike"));
    }

}
