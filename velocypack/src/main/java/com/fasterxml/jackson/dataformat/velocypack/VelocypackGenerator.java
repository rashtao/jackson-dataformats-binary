package com.fasterxml.jackson.dataformat.velocypack;

import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.ValueType;
import com.arangodb.velocypack.exception.VPackBuilderException;
import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.GeneratorBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.PackageVersion;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Mark Vollmary
 * @author Michele Rastelli
 * <p>
 * TODO: allocate buffers inside VPackBuilder using ioCtxt
 */
public class VelocypackGenerator extends GeneratorBase {

    protected final VPackBuilder builder;
    protected String attribute;
    protected final IOContext ioContext;
    protected final OutputStream out;

    public VelocypackGenerator(IOContext ioCtxt, int streamWriteFeatures, ObjectCodec codec, OutputStream out) {
        super(streamWriteFeatures, codec);
        ioContext = ioCtxt;
        this.out = out;
        builder = new VPackBuilder();
        attribute = null;
    }

    @Override
    public void writeStartObject(Object o) throws IOException {
        try {
            builder.add(attribute, ValueType.OBJECT);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeStartObject() throws IOException {
        try {
            builder.add(attribute, ValueType.OBJECT);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeEndObject() throws IOException {
        try {
            builder.close();
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeFieldName(String s) throws IOException {

    }

    @Override
    public void writeFieldName(SerializableString serializableString) throws IOException {
        attribute = serializableString.getValue();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void writeString(String s) throws IOException {
        try {
            builder.add(attribute, s);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeString(char[] chars, int i, int i1) throws IOException {
        try {
            builder.add(attribute, new String(chars, i, i1));
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeRawUTF8String(byte[] bytes, int i, int i1) throws IOException {
        try {
            builder.add(attribute, new String(bytes, i, i1));
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeUTF8String(byte[] bytes, int i, int i1) throws IOException {
        try {
            builder.add(attribute, new String(bytes, i, i1));
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeRaw(String s) throws IOException {

    }

    @Override
    public void writeRaw(String s, int i, int i1) throws IOException {

    }

    @Override
    public void writeRaw(char[] chars, int i, int i1) throws IOException {

    }

    @Override
    public void writeRaw(char c) throws IOException {
        try {
            builder.add(attribute, c);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeBinary(Base64Variant base64Variant, byte[] bytes, int i, int i1) throws IOException {
        try {
            builder.add(attribute, base64Variant.encode(bytes, false));
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    public void writeVPack(final VPackSlice vpack) throws IOException {
        try {
            builder.add(attribute, vpack);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeNumber(int i) throws IOException {
        try {
            builder.add(attribute, i);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeNumber(long l) throws IOException {
        try {
            builder.add(attribute, l);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeNumber(BigInteger bigInteger) throws IOException {
        try {
            builder.add(attribute, bigInteger);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeNumber(double v) throws IOException {
        try {
            builder.add(attribute, v);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeNumber(float v) throws IOException {
        try {
            builder.add(attribute, v);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeNumber(BigDecimal bigDecimal) throws IOException {
        try {
            builder.add(attribute, bigDecimal);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeNumber(String s) throws IOException {

    }

    @Override
    public void writeBoolean(boolean b) throws IOException {
        try {
            builder.add(attribute, b);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeNull() throws IOException {
        try {
            builder.add(attribute, ValueType.NULL);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeStartArray() throws IOException {
        try {
            builder.add(attribute, ValueType.ARRAY);
            attribute = null;
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeEndArray() throws IOException {
        try {
            builder.close();
        } catch (final VPackBuilderException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        out.write(builder.slice().getBuffer());
        super.close();
    }

    @Override
    protected void _releaseBuffers() {

    }

    @Override
    protected void _verifyValueWrite(String s) throws IOException {

    }

    /*
    /**********************************************************************
    /* Versioned
    /**********************************************************************
     */

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    /*
    /**********************************************************************
    /* Capability introspection
    /**********************************************************************
     */

    @Override
    public boolean canWriteBinaryNatively() {
        return true;
    }

    /*
    /**********************************************************
    /* Overridden methods, configuration
    /**********************************************************
     */

    @Override
    public Object getOutputTarget() {
        return out;
    }

}
