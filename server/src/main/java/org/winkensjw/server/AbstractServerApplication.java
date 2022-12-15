package org.winkensjw.server;

import org.jboss.logging.Logger;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;

public abstract class AbstractServerApplication implements QuarkusApplication {

    private static final Logger LOG = Logger.getLogger(AbstractServerApplication.class);

    @Override
    public int run(String... args) throws Exception {
        LOG.info("Bothamsta starting up...");
        initialize();
        LOG.info("Bothamsta successfully started.");
        Quarkus.waitForExit();
        return 0;
    }

    protected abstract void initialize();
}