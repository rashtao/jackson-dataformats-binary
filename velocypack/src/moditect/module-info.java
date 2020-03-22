module com.fasterxml.jackson.dataformat.velocypack {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports com.fasterxml.jackson.dataformat.velocypack;

    provides com.fasterxml.jackson.core.TokenStreamFactory with
        com.fasterxml.jackson.dataformat.velocypack.VelocypackFactory;
}
