package org.winkensjw.platform.configuration;

import org.winkensjw.platform.configuration.property.AbstractBooleanProperty;
import org.winkensjw.platform.configuration.property.AbstractLongProperty;
import org.winkensjw.platform.configuration.property.AbstractStringProperty;

// FIXME jwi move/spli properties
public class BothamstaProperties {

    private BothamstaProperties() {
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

    public static class TwitterUserIdProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.userId";
        }
    }

    public static class TwitterOAuthConsumerKeyProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.oAuthConsumerKey";
        }
    }


    public static class TwitterOAuthConsumerSecretProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.oAuthConsumerSecret";
        }
    }


    public static class TwitterOAuthAccessTokenProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.oAuthAccessToken";
        }
    }


    public static class TwitterOAuthAccessTokenSecretProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.twitter.oAuthAccessTokenSecret";
        }
    }


    public static class YoutubeBotNameProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.youtube.botName";
        }
    }

    public static class YoutubeApiKeyProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.youtube.apiKey";
        }
    }

    public static class YoutubeChannelNameProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.youtube.channelName";
        }
    }


    public static class YoutubeChannelUploadsPlaylistIdProperty extends AbstractStringProperty {
        @Override
        public String getId() {
            return "bothamsta.server.youtube.channelUploadsPlaylistId";
        }
    }


    public static class YoutubeCheckUploadsIntervalMinutesProperty extends AbstractLongProperty {
        @Override
        public String getId() {
            return "bothamsta.server.youtube.checkUploadsIntervalMinutes";
        }
    }

    public static class YoutubeMaxPlaylistItemsProperty extends AbstractLongProperty {
        @Override
        public String getId() {
            return "bothamsta.server.youtube.maxPlaylistItems";
        }
    }
}
