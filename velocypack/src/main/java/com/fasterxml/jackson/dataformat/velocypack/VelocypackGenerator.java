package com.fasterxml.jackson.dataformat.velocypack;

import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.ValueType;
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
 * @author Michele Rastelli
 */
public class VelocypackGenerator extends GeneratorBase {

    private VPackBuilder builder = new VPackBuilder();

    /*
    /**********************************************************************
    /* Configuration
    /**********************************************************************
     */

    final protected IOContext _ioContext;

    final protected OutputStream _out;

    /*
    /**********************************************************
    /* Output buffering
    /**********************************************************************
     */

    /**
     * Intermediate buffer in which contents are buffered before
     * being written using {@link #_out}.
     */
    protected byte[] _outputBuffer;

    /**
     * Pointer to the next available byte in {@link #_outputBuffer}
     */
    protected int _outputTail = 0;

    /**
     * Offset to index after the last valid index in {@link #_outputBuffer}.
     * Typically same as length of the buffer.
     */
    protected final int _outputEnd;

    /**
     * Let's keep track of how many bytes have been output, may prove useful
     * when debugging. This does <b>not</b> include bytes buffered in
     * the output buffer, just bytes that have been written using underlying
     * stream writer.
     */
    protected int _bytesWritten;

    public VelocypackGenerator(IOContext ioCtxt, int streamWriteFeatures, ObjectCodec codec, OutputStream out) {
        super(streamWriteFeatures, codec);
        _ioContext = ioCtxt;
        _out = out;
        _outputBuffer = ioCtxt.allocWriteEncodingBuffer();
        _outputEnd = _outputBuffer.length;

    }

    @Override
    public void writeStartObject(Object o) throws IOException {
        builder.add(ValueType.OBJECT);
    }

    @Override
    public void writeEndObject() throws IOException {
        builder.close();
    }

    @Override
    public void writeFieldName(String s) throws IOException {

    }

    @Override
    public void writeFieldName(SerializableString serializableString) throws IOException {
        builder.add(serializableString.getValue());
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void writeString(String s) throws IOException {
        builder.add(s);
    }

    @Override
    public void writeString(char[] chars, int i, int i1) throws IOException {

    }

    @Override
    public void writeRawUTF8String(byte[] bytes, int i, int i1) throws IOException {

    }

    @Override
    public void writeUTF8String(byte[] bytes, int i, int i1) throws IOException {

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

    }

    @Override
    public void writeBinary(Base64Variant base64Variant, byte[] bytes, int i, int i1) throws IOException {

    }

    @Override
    public void writeNumber(int i) throws IOException {

    }

    @Override
    public void writeNumber(long l) throws IOException {

    }

    @Override
    public void writeNumber(BigInteger bigInteger) throws IOException {

    }

    @Override
    public void writeNumber(double v) throws IOException {

    }

    @Override
    public void writeNumber(float v) throws IOException {

    }

    @Override
    public void writeNumber(BigDecimal bigDecimal) throws IOException {

    }

    @Override
    public void writeNumber(String s) throws IOException {

    }

    @Override
    public void writeBoolean(boolean b) throws IOException {

    }

    @Override
    public void writeNull() throws IOException {

    }


    @Override
    public void close() throws IOException {
        _out.write(builder.slice().getBuffer());
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

    @Override
    public void writeStartArray() throws IOException {

    }

    @Override
    public void writeEndArray() throws IOException {

    }

    @Override
    public void writeStartObject() throws IOException {

    }

    /*
    /**********************************************************
    /* Overridden methods, configuration
    /**********************************************************
     */

    @Override
    public Object getOutputTarget() {
        return _out;
    }

}
