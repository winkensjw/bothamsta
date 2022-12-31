package org.winkensjw.server;

import org.jboss.logging.Logger;
import org.wildfly.common.Assert;
import org.winkensjw.platform.components.ComponentsRegistry;
import org.winkensjw.platform.db.DB;
import org.winkensjw.twitch.TwitchComponent;
import org.winkensjw.twitter.TwitterComponent;
import org.winkensjw.youtube.YoutubeComponent;

import static org.winkensjw.platform.db.schema.Tables.BH_DUAL;

public class ServerApplication extends AbstractServerApplication {

    private static final Logger LOG = Logger.getLogger(ServerApplication.class);

    @Override
    protected void initialize() {
        ensureDatabase();
        initializeComponents();
    }

    protected void ensureDatabase() {
        LOG.info("Performing Database sanity check...");
        // check DB is available by pinging dual
        Assert.assertNotNull(DB.uniqueResult(DB.createQuery().select().from(BH_DUAL)));
        LOG.info("Database check successful!");
    }

    protected void initializeComponents() {
        LOG.info("Initializing Components...");
        ComponentsRegistry.register(new TwitchComponent());
        ComponentsRegistry.register(new TwitterComponent());
        ComponentsRegistry.register(new YoutubeComponent());
        LOG.info("Components initialized successfully!");
    }
}
