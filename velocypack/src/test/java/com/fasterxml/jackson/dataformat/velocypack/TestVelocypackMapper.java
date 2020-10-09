package com.fasterxml.jackson.dataformat.velocypack;

import com.arangodb.velocypack.VPackParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import java.io.IOException;

/**
 * @author Michele Rastelli
 */
public class TestVelocypackMapper extends VelocypackMapper {

    private static final VPackParser PARSER = new VPackParser.Builder().build();


    private static final long serialVersionUID = 1L;

    public static class Builder extends MapperBuilder<TestVelocypackMapper, TestVelocypackMapper.Builder> {
        public Builder(TestVelocypackMapper m) {
            super(m);
        }
    }

    public static TestVelocypackMapper.Builder testBuilder() {
        return new TestVelocypackMapper.Builder(new TestVelocypackMapper());
    }

    public static TestVelocypackMapper.Builder testBuilder(VelocypackFactory jf) {
        return new TestVelocypackMapper.Builder(new TestVelocypackMapper(jf));
    }

    public TestVelocypackMapper() {
        this(new VelocypackFactory());
    }

    public TestVelocypackMapper(VelocypackFactory jf) {
        super(jf);
    }

    protected TestVelocypackMapper(TestVelocypackMapper src) {
        super(src);
    }

    @Override
    public TestVelocypackMapper copy() {
        _checkInvalidCopy(TestVelocypackMapper.class);
        return new TestVelocypackMapper(this);
    }

    @Override
    public <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException {
        try {
            return super.readValue(PARSER.fromJson(content, true).getBuffer(), valueType);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException, JsonMappingException {
        try {
            return super.readValue(PARSER.fromJson(content, true).getBuffer(), valueTypeRef);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T readValue(String content, JavaType valueType) throws JsonProcessingException, JsonMappingException {
        try {
            return super.readValue(PARSER.fromJson(content, true).getBuffer(), valueType);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
