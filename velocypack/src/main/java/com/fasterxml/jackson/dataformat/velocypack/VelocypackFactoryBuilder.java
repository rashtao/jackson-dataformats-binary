package com.fasterxml.jackson.dataformat.velocypack;


import com.fasterxml.jackson.core.TSFBuilder;

/**
 * {@link com.fasterxml.jackson.core.TSFBuilder}
 * implementation for constructing {@link VelocypackFactory}
 * instances.
 *
 * @since 3.0
 */
public class VelocypackFactoryBuilder extends TSFBuilder<VelocypackFactory, VelocypackFactoryBuilder> {
    public VelocypackFactoryBuilder() {
        super();
    }

    public VelocypackFactoryBuilder(VelocypackFactory base) {
        super(base);
    }

    @Override
    public VelocypackFactory build() {
        // 28-Dec-2017, tatu: No special settings beyond base class ones, so:
        return new VelocypackFactory(this);
    }
}
