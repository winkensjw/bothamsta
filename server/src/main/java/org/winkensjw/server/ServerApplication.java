package org.winkensjw.server;

import org.jboss.logging.Logger;
import org.jooq.Record;
import org.wildfly.common.Assert;
import org.winkensjw.platform.components.ComponentsRegistry;
import org.winkensjw.platform.db.DB;
import org.winkensjw.platform.db.schema.Tables;
import org.winkensjw.twitch.TwitchComponent;
import org.winkensjw.twitter.TwitterComponent;
import org.winkensjw.youtube.YoutubeComponent;

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
        Record record = DB.uniqueResult(DB.createQuery().selectOne().from(Tables.BH_DUAL).getSQL());
        Assert.assertNotNull(record);
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
