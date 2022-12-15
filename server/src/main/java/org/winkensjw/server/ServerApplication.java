package org.winkensjw.server;

import org.winkensjw.platform.components.ComponentsRegistry;
import org.winkensjw.twitch.TwitchComponent;
import org.winkensjw.twitter.TwitterComponent;

public class ServerApplication extends AbstractServerApplication {


    @Override
    protected void initialize() {
        initializeComponents();
    }

    protected void initializeComponents() {
        ComponentsRegistry.registerComponent(new TwitchComponent());
        ComponentsRegistry.registerComponent(new TwitterComponent());
    }
}
