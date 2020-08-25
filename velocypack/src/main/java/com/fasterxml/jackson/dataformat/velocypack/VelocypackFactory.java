package com.fasterxml.jackson.dataformat.velocypack;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.format.InputAccessor;
import com.fasterxml.jackson.core.format.MatchStrength;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.json.PackageVersion;

import java.io.*;
import java.net.URL;

public class VelocypackFactory extends JsonFactory {
    private static final long serialVersionUID = 1;

    /*
    /**********************************************************
    /* Constants
    /**********************************************************
     */

    /**
     * Name used to identify Velocypack format.
     * (and returned by {@link #getFormatName()}
     */
    public static final String FORMAT_NAME_VELOCYPACK = "Velocypack";

    /*
    /**********************************************************
    /* Factory construction, configuration
    /**********************************************************
     */

    public VelocypackFactory() {
    }

    public VelocypackFactory(ObjectCodec codec) {
        super(codec);
    }

    protected VelocypackFactory(VelocypackFactory src, ObjectCodec oc) {
        super(src, oc);
    }

    protected VelocypackFactory(VelocypackFactoryBuilder b) {
        super(b, false);
    }

    @Override
    public VelocypackFactoryBuilder rebuild() {
        return new VelocypackFactoryBuilder(this);
    }

    /**
     * Main factory method to use for constructing {@link VelocypackFactory} instances with
     * different configuration.
     */
    public static VelocypackFactoryBuilder builder() {
        return new VelocypackFactoryBuilder();
    }

    @Override
    public VelocypackFactory copy() {
        _checkInvalidCopy(VelocypackFactory.class);
        return new VelocypackFactory(this, null);
    }

    /*
    /**********************************************************
    /* Serializable overrides
    /**********************************************************
     */

    /**
     * Method that we need to override to actually make restoration go
     * through constructors etc.
     * Also: must be overridden by sub-classes as well.
     */
    @Override
    protected Object readResolve() {
        return new VelocypackFactory(this, _objectCodec);
    }

    /*
    /**********************************************************
    /* Versioned
    /**********************************************************
     */

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    /*
    /**********************************************************
    /* Format detection functionality
    /**********************************************************
     */

    @Override
    public String getFormatName() {
        return FORMAT_NAME_VELOCYPACK;
    }

    /**
     * Sub-classes need to override this method
     */
    @Override
    public MatchStrength hasFormat(InputAccessor acc) {
        // TODO, if possible... probably isn't?
        return MatchStrength.INCONCLUSIVE;
    }

    /*
    /**********************************************************
    /* Capability introspection
    /**********************************************************
     */

    @Override
    public boolean requiresPropertyOrdering() {
        return false;
    }

    @Override
    public boolean canHandleBinaryNatively() {
        return true;
    }

    @Override
    public boolean canUseCharArrays() {
        return false;
    }

    /*
    /**********************************************************
    /* Overridden parser factory methods
    /**********************************************************
     */

    @SuppressWarnings("resource")
    @Override
    public VelocypackParser createParser(File f) throws IOException {
        final IOContext ctxt = _createContext(f, true);
        return _createParser(_decorate(new FileInputStream(f), ctxt), ctxt);
    }

    @Override
    public VelocypackParser createParser(URL url) throws IOException {
        final IOContext ctxt = _createContext(url, true);
        return _createParser(_decorate(_optimizedStreamFromURL(url), ctxt), ctxt);
    }

    @Override
    public VelocypackParser createParser(InputStream in) throws IOException {
        final IOContext ctxt = _createContext(in, false);
        return _createParser(_decorate(in, ctxt), ctxt);
    }

    @Override
    public VelocypackParser createParser(byte[] data) {
        return _createParser(data, 0, data.length, _createContext(data, true));
    }

    @SuppressWarnings("resource")
    @Override
    public VelocypackParser createParser(byte[] data, int offset, int len) throws IOException {
        IOContext ctxt = _createContext(data, true);
        if (_inputDecorator != null) {
            InputStream in = _inputDecorator.decorate(ctxt, data, 0, data.length);
            if (in != null) {
                return _createParser(in, ctxt);
            }
        }
        return _createParser(data, offset, len, ctxt);
    }

    /*
    /**********************************************************
    /* Overridden generator factory methods
    /**********************************************************
     */

    @Override
    public VelocypackGenerator createGenerator(OutputStream out, JsonEncoding enc) throws IOException {
        IOContext ctxt = _createContext(out, false);
        ctxt.setEncoding(enc);
        return createVelocypackGenerator(ctxt, _generatorFeatures, _objectCodec, _decorate(out, ctxt));
    }

    /**
     * Method for constructing {@link JsonGenerator} for generating
     * velocypack-encoded output.
     * <p>
     * Since velocypack format always uses UTF-8 internally, no encoding need
     * to be passed to this method.
     */
    @Override
    public VelocypackGenerator createGenerator(OutputStream out) throws IOException {
        IOContext ctxt = _createContext(out, false);
        return createVelocypackGenerator(ctxt, _generatorFeatures, _objectCodec, _decorate(out, ctxt));
    }

    /*
    /******************************************************
    /* Overridden internal factory methods
    /******************************************************
     */

    @Override
    protected VelocypackParser _createParser(InputStream in, IOContext ctxt) {
        throw new UnsupportedOperationException("Stream decoding is not supported!");
    }

    @Override
    protected JsonParser _createParser(Reader r, IOContext ctxt) {
        return nonByteSource();
    }

    @Override
    protected JsonParser _createParser(char[] data, int offset, int len, IOContext ctxt,
                                       boolean recyclable) {
        return nonByteSource();
    }

    @Override
    protected VelocypackParser _createParser(byte[] data, int offset, int len, IOContext ctxt) {
        return new VelocypackParser(ctxt, _parserFeatures,
                _objectCodec, data, offset, false);
    }

    @Override
    protected VelocypackGenerator _createGenerator(Writer out, IOContext ctxt) {
        return nonByteTarget();
    }

    @Override
    protected VelocypackGenerator _createUTF8Generator(OutputStream out, IOContext ctxt) {
        return createVelocypackGenerator(ctxt, _generatorFeatures, _objectCodec, out);
    }

    @Override
    protected Writer _createWriter(OutputStream out, JsonEncoding enc, IOContext ctxt) {
        return nonByteTarget();
    }

    private final VelocypackGenerator createVelocypackGenerator(IOContext ctxt,
                                                                int stdFeat, ObjectCodec codec, OutputStream out) {
        return new VelocypackGenerator(ctxt, stdFeat, codec, out);
    }

    protected <T> T nonByteSource() {
        throw new UnsupportedOperationException("Can not create parser for non-byte-based source");
    }

    protected <T> T nonByteTarget() {
        throw new UnsupportedOperationException("Can not create generator for non-byte-based target");
    }
}