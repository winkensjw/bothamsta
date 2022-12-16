package org.winkensjw.twitch;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class TwitchComponentProducer {
    @Produces
    @ApplicationScoped
    public TwitchComponent twitchComponent() {
        return new TwitchComponent();
    }
}
