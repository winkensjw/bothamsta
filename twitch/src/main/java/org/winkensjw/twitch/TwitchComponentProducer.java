package org.winkensjw.twitch;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@SuppressWarnings("unused")
@Singleton
public class TwitchComponentProducer {
    @Produces
    @ApplicationScoped
    public TwitchComponent twitchComponent() {
        return new TwitchComponent();
    }
}
