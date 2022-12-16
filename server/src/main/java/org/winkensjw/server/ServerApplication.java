package org.winkensjw.server;

import org.winkensjw.twitch.TwitchComponent;
import org.winkensjw.twitter.TwitterComponent;

import javax.inject.Inject;

public class ServerApplication extends AbstractServerApplication {
    @Inject
    TwitterComponent m_twitterComponent;

    @Inject
    TwitchComponent m_twitchComponent;

    @Override
    protected void initialize() {
        initializeComponents();
    }

    protected void initializeComponents() {
        m_twitchComponent.start();
        m_twitterComponent.start();
    }
}
