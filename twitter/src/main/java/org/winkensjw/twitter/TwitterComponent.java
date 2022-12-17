package org.winkensjw.twitter;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.Configuration;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;
import org.jboss.logging.Logger;
import org.winkensjw.platform.components.IComponent;
import org.winkensjw.platform.components.IComponentNotification;
import org.winkensjw.platform.components.TwitterCreateTweetComponentNotification;
import org.winkensjw.platform.configuration.BothamstaProperties.*;
import org.winkensjw.platform.configuration.util.CONFIG;
import org.winkensjw.platform.util.StringUtility;
import org.winkensjw.twitter.auth.TwitterAuthenticator;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.UploadedMedia;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TwitterComponent implements IComponent {

    private static final Logger LOG = Logger.getLogger(TwitterComponent.class);

    private TwitterCredentialsOAuth2 m_authToken = null;
    private final Scheduler m_scheduler = new Scheduler();

    public void setAuthToken(TwitterCredentialsOAuth2 authToken) {
        m_authToken = authToken;
    }

    public TwitterCredentialsOAuth2 getAuthToken() {
        return m_authToken;
    }

    public Scheduler getScheduler() {
        return m_scheduler;
    }

    @Override
    public void start() {
        LOG.info("Starting twitter component...");
        TwitterAuthenticator authenticator = new TwitterAuthenticator();
        authenticator.authenticate();
        waitForAuthToken();
        startLiking();
    }

    protected TwitterApi getApi() {
        BothamstaTwitterApiClient bothamstaTwitterApiClient = new BothamstaTwitterApiClient();
        bothamstaTwitterApiClient.setTwitterCredentials(getAuthToken());
        Configuration.setDefaultApiClient(bothamstaTwitterApiClient);
        return new TwitterApi(bothamstaTwitterApiClient);
    }

    protected Twitter getMediaApi() {
        return Twitter.newBuilder()
                .oAuthConsumer(CONFIG.get(TwitterOAuthConsumerKeyProperty.class), CONFIG.get(TwitterOAuthConsumerSecretProperty.class))
                .oAuthAccessToken(CONFIG.get(TwitterOAuthAccessTokenProperty.class), CONFIG.get(TwitterOAuthAccessTokenSecretProperty.class)).build();
    }

    protected void startLiking() {
        getScheduler().schedule(this::likeTweets,
                Schedules.afterInitialDelay(
                        Schedules.fixedDelaySchedule(
                                Duration.ofMinutes(CONFIG.get(TwitterMaxTweetAgeMinutesProperty.class))),
                        Duration.ZERO));
    }

    protected void likeTweets() {
        try {
            int number = 1;
            TwitterAuthenticator authenticator = new TwitterAuthenticator();
            authenticator.refreshAccessToken(getAuthToken());
            TwitterApi apiInstance = getApi();
            for (Tweet tweet : getTweets(apiInstance)) {
                likeTweet(apiInstance, tweet);
                printTweetInfo(tweet, number);
                waitBeforeNextLike();
                number++;
            }
        } catch (ApiException e) {
            LOG.error("Error calling twitter API!", e);
        }
    }

    protected List<Tweet> getTweets(TwitterApi apiInstance) throws ApiException {
        Set<String> tweetFields = new HashSet<>(Set.of("author_id", "id", "created_at", "lang"));
        String searchString = CONFIG.get(TwitterSearchQueryProperty.class);
        OffsetDateTime startTime = OffsetDateTime.now().minus(CONFIG.get((TwitterMaxTweetAgeMinutesProperty.class)),
                ChronoUnit.MINUTES);
        OffsetDateTime endTime = OffsetDateTime.now().minus(11, ChronoUnit.SECONDS);
        List<Tweet> tweets = apiInstance.tweets()
                .tweetsRecentSearch(searchString)
                .startTime(startTime)
                .endTime(endTime)
                .maxResults(100)
                .sortOrder("recency")
                .tweetFields(tweetFields)
                .execute().getData();
        return tweets != null ? tweets : Collections.emptyList();
    }

    protected void writeTweet(String text, String imgUrl) throws ApiException {
        Long imgId = uploadMedia(imgUrl);
        TweetCreateRequest tweet = new TweetCreateRequest().text(text);
        if (imgId != null) {
            tweet.media(new TweetCreateRequestMedia()
                    .mediaIds(List.of(String.valueOf(imgId))));
        }
        TweetCreateResponse response = getApi().tweets().createTweet(tweet).execute();
        LOG.infov("Tweeted: {0}", response);
    }

    protected Long uploadMedia(String imgUrl) {
        File image = downloadImage(imgUrl);
        if (image == null) {
            return null;
        }
        try {
            UploadedMedia media = getMediaApi().v1().tweets().uploadMedia(image);
            return media.getMediaId();
        } catch (TwitterException e) {
            LOG.errorv(e, "Failed to upload image to twitter. URL: {0}", imgUrl);
            return null;
        }
    }

    protected File downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            BufferedImage dlImage = ImageIO.read(url);
            File image = File.createTempFile(UUID.randomUUID().toString(), ".jpg");
            ImageIO.write(dlImage, "jpg", image);
            return image;
        } catch (IOException e) {
            LOG.errorv(e, "Failed to download image. URL: {0}", imageUrl);
            return null;
        }
    }


    protected void likeTweet(TwitterApi apiInstance, Tweet tweet) throws ApiException {
        if (CONFIG.get(TwitterLikeUserIdProperty.class).equals(tweet.getAuthorId())) {
            // don't like your own tweets
            return;
        }
        UsersLikesCreateRequest request = new UsersLikesCreateRequest().tweetId(tweet.getId());
        apiInstance.tweets()
                .usersIdLike(CONFIG.get(TwitterLikeUserIdProperty.class))
                .usersLikesCreateRequest(request)
                .execute();
    }

    protected void printTweetInfo(Tweet tweet, int number) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("\nTweet Info: \n"
                    + "--------------------------------------------------------\n"
                    + "No: " + number + "\n"
                    + "ID: " + tweet.getId() + "\n"
                    + "Language: " + tweet.getLang() + "\n"
                    + "Author ID: " + tweet.getAuthorId() + "\n"
                    + "Text: \n" + tweet.getText() + "\n"
                    + "--------------------------------------------------------\n");
        } else {
            LOG.infov("Tweet by {0} with text: {1} media: {2}", tweet.getAuthorId(), StringUtility.removeLinebreaks(tweet.getText()));
        }
    }

    protected synchronized void waitForAuthToken() {
        while (getAuthToken() == null) {
            LOG.info("Waiting for authentication.");
            try {
                wait(1000L);
            } catch (InterruptedException e) {
                LOG.error("Interrupted while waiting for authentication! Twitter component not started.", e);
                return;
            }
        }
    }

    protected synchronized void waitBeforeNextLike() {
        try {
            wait(new Random().nextInt(4000) + 1000);
        } catch (InterruptedException e) {
            LOG.error("Interrupted while waiting to next like!", e);
        }
    }

    @Override
    public void handleNotification(IComponentNotification notification) {
        try {
            if (notification instanceof TwitterCreateTweetComponentNotification twitterNotifation) {
                writeTweet(twitterNotifation.getText(), twitterNotifation.getImgUrl());
            }
        } catch (Exception e) {
            LOG.error("Error handling notification!", e);
        }
    }
}
