package org.winkensjw.platform.configuration;

import org.winkensjw.platform.configuration.property.AbstractBooleanProperty;
import org.winkensjw.platform.configuration.property.AbstractLongProperty;
import org.winkensjw.platform.configuration.property.AbstractStringProperty;

// FIXME jwi move properties
public class BothamstaServerProperties {

    private BothamstaServerProperties() {
        // no instance needed, just classes
    }

    public static class DebugProperty extends AbstractBooleanProperty {
        @Override
        public String getId() {
            return "bothamsta.server.debug";
        }
    }

    public static class TwitchOAuthTokenProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitch.oAuthToken";
        }
    }

    public static class DbUserNameProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.db.userName";
        }
    }

    public static class DbPasswordProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.db.password";
        }
    }

    public static class DbJdbcUrlProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.db.jdbcUrl";
        }
    }

    public static class TwitterClientIDProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.clientId";
        }
    }

    public static class TwitterClientSecretProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.clientSecret";
        }
    }

    public static class TwitterSearchQueryProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.searchQuery";
        }
    }

    public static class TwitterMaxTweetAgeMinutesProperty extends AbstractLongProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.maxTweegAgeMinutes";
        }
    }

    public static class TwitterLikeUserIdProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.likeUserId";
        }
    }
}
