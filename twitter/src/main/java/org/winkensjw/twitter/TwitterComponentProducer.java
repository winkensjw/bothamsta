package org.winkensjw.twitter;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

@Singleton
public class TwitterComponentProducer {
    @Produces
    @ApplicationScoped
    public TwitterComponent twitterComponent() {
        return new TwitterComponent();
    }
}
