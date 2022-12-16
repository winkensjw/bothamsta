package org.winkensjw.twitter;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.Configuration;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.Tweet;
import com.twitter.clientlib.model.UsersLikesCreateRequest;
import org.jboss.logging.Logger;
import org.winkensjw.platform.components.IComponent;
import org.winkensjw.platform.configuration.BothamstaServerProperties.TwitterLikeUserIdProperty;
import org.winkensjw.platform.configuration.BothamstaServerProperties.TwitterMaxTweetAgeMinutesProperty;
import org.winkensjw.platform.configuration.BothamstaServerProperties.TwitterSearchQueryProperty;
import org.winkensjw.platform.configuration.util.CONFIG;
import org.winkensjw.twitter.auth.TwitterAuthenticator;

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
        LOG.info("Starting twitter component.");
        TwitterAuthenticator authenticator = new TwitterAuthenticator();
        authenticator.authenticate();

        waitForAuthToken();

        LOG.info("Authentication successful.");

        startLiking();
    }

    protected TwitterApi getApi() {
        BothamstaTwitterApiClient bothamstaTwitterApiClient = new BothamstaTwitterApiClient();
        bothamstaTwitterApiClient.setTwitterCredentials(getAuthToken());
        Configuration.setDefaultApiClient(bothamstaTwitterApiClient);
        return new TwitterApi(bothamstaTwitterApiClient);
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
        LOG.info("\nTweet Info: \n"
                + "--------------------------------------------------------\n"
                + "No: " + number + "\n"
                + "ID: " + tweet.getId() + "\n"
                + "Language: " + tweet.getLang() + "\n"
                + "Author ID: " + tweet.getAuthorId() + "\n"
                + "Text: \n" + tweet.getText() + "\n"
                + "--------------------------------------------------------\n");
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
}
