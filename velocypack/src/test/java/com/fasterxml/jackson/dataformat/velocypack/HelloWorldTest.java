package com.fasterxml.jackson.dataformat.velocypack;

import com.arangodb.velocypack.VPackSlice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Michele Rastelli
 */
public class HelloWorldTest {

    @Test
    public void testSerialize() throws JsonProcessingException {
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

    @Test
    public void testDeserialize() throws IOException {
        Person p = new Person("mike");
        ObjectMapper mapper = new ObjectMapper(new VelocypackFactory());
        final ObjectWriter writer = mapper.writer();
        byte[] bytes = writer.writeValueAsBytes(p);
        Person readPerson = mapper.readerFor(Person.class).readValue(bytes);
        assertThat(readPerson.getName(), is(p.getName()));
    }

    @Test
    public void testJsonNode() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new VelocypackFactory());
        ObjectNode input = JsonNodeFactory.instance.objectNode()
                .put("name", "mike");
        System.out.println(input);
        byte[] bytes = mapper.writer().writeValueAsBytes(input);
        System.out.println(new VPackSlice(bytes));
        ObjectNode objectNode = mapper.readerFor(ObjectNode.class).readValue(bytes);
        JsonNode jsonNode = objectNode.get("name");
        assertThat(jsonNode.isTextual(), is(true));
        assertThat(jsonNode.asText(), is("mike"));
    }

    @Test
    public void testDbEntity() throws IOException {
        DbEntity e = new DbEntity();
        e.setKey("key");
        ObjectMapper mapper = new ObjectMapper(new VelocypackFactory());
        final ObjectWriter writer = mapper.writer();
        byte[] bytes = writer.writeValueAsBytes(e);
        System.out.println(new VPackSlice(bytes));
        DbEntity readEntity = mapper.readerFor(DbEntity.class).readValue(bytes);
        assertThat(readEntity.getKey(), is(e.getKey()));
    }

}
