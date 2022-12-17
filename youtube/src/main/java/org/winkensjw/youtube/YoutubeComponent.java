package org.winkensjw.youtube;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Builder;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import org.jboss.logging.Logger;
import org.wildfly.common.Assert;
import org.winkensjw.platform.components.ComponentsRegistry;
import org.winkensjw.platform.components.IComponent;
import org.winkensjw.platform.components.IComponentNotification;
import org.winkensjw.platform.components.TwitterCreateTweetComponentNotification;
import org.winkensjw.platform.configuration.BothamstaProperties.*;
import org.winkensjw.platform.configuration.util.CONFIG;
import org.winkensjw.platform.db.DB;
import org.winkensjw.platform.db.schema.tables.records.BhYoutubeVideoRecord;
import org.winkensjw.platform.util.CollectionUtility;
import org.winkensjw.platform.util.StringUtility;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.winkensjw.platform.db.schema.Tables.BH_YOUTUBE_VIDEO;

@SuppressWarnings("deprecation")
public class YoutubeComponent implements IComponent {
    private static final Logger LOG = Logger.getLogger(YoutubeComponent.class);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final Scheduler m_scheduler = new Scheduler();
    private static final String YOUTUBE_BASE_URL = "https://youtu.be/";
    private static final String VIDEO_TITLE_SEPARATOR = "|";
    private static final String VIDEO_HASH_TAG_CHAR = "#";
    private static final String VIDEO_TITLE_LETS_PLAY = "(Let's Play)";

    public Scheduler getScheduler() {
        return m_scheduler;
    }

    @Override
    public void start() {
        LOG.info("Starting Youtube component...");
        sanityCheck();
        getScheduler().schedule(this::handleVideos, Schedules.afterInitialDelay(
                Schedules.fixedDelaySchedule(Duration.ofMinutes(CONFIG.get(YoutubeCheckUploadsIntervalMinutesProperty.class))),
                Duration.ZERO));
    }

    /**
     * Sanity check by selecting count of known Youtube videos,
     * should be > 0 or we have made a mistake that could cause a lot of tweets to be sent
     */
    protected void sanityCheck() {
        Assert.assertTrue(DB.count(BH_YOUTUBE_VIDEO) > 0);
    }

    protected void handleVideos() {
        List<Video> unknownVideos = getUnknownVideos();
        // sanity check, should only be one
        if (CollectionUtility.size(unknownVideos) > 1) {
            LOG.warnv("Found more than one unknown youtube video, which should propably not happen. Won't do anything! Videos: {0}", unknownVideos);
            return;
        }
        unknownVideos.forEach(this::handleVideo);
    }

    protected void handleVideo(Video video) {
        try {
            if (!CONFIG.get(YoutubeChannelNameProperty.class).equals(video.getChannelName())) {
                LOG.warnv("Found video that does not belong to target channel! Channel: {0} Title: {1}", video.getChannelName(), video.getTitle());
                return;
            }
            printYoutubeInfo(video);
            storeYoutubeVideo(video);
            // always perform these after store, so even in case of failure its only done once
            ComponentsRegistry.notifyComponents(new TwitterCreateTweetComponentNotification(createTweetText(video), video.getThumbnailUrl()));
        } catch (Exception e) {
            LOG.errorv(e, "Failed to handle video with ID: {0} Title: {1}", video.getId(), video.getTitle());
        }
    }

    protected String createTweetText(Video video) {
        return getTweetVideoTitleText(video) + "\n" +
                "\n" +
                YOUTUBE_BASE_URL + video.getVideoId() + "\n" +
                "\n" +
                "✈️\uD83D\uDC39\n" +
                "----\n" +
                "#twitch #twitchDE #youtube #yt #video" + " " + getAdditionalHashTags(video);
    }

    protected String getTweetVideoTitleText(Video video) {
        String title = video.getTitle();
        int separatorPos = title.indexOf(VIDEO_TITLE_SEPARATOR);
        if (separatorPos < 0) {
            return title;
        }
        // title is of format <TITLE> | <GAME> (Let's Play)
        // transform to tweet text <TITLE> #<GAME WITHOUT SPACES>
        String actualTitle = title.substring(0, separatorPos);
        String game = StringUtility.removeSuffixes(title.substring(separatorPos + 1), VIDEO_TITLE_LETS_PLAY);
        String gameHashTag = game.replace(" ", "");
        return StringUtility.join(" #", actualTitle, gameHashTag);
    }

    protected String getAdditionalHashTags(Video video) {
        // read hashtags from video description and add to end of tweet
        int hashTagPos = video.getDescription().indexOf(VIDEO_HASH_TAG_CHAR);
        if (hashTagPos < 0) {
            return "";
        }
        return video.getDescription().substring(hashTagPos);
    }


    protected void printYoutubeInfo(Video video) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("\nYoutube Video Info: \n"
                    + "--------------------------------------------------------\n"
                    + "ID: " + video.getId() + "\n"
                    + "Channel: " + video.getChannelName() + "\n"
                    + "Title: \n" + video.getTitle() + "\n"
                    + "Thumbnail url: \n" + video.getThumbnailUrl() + "\n"
                    + "--------------------------------------------------------\n");
        } else {
            LOG.infov("Youtube video found with title: {0}", video.getTitle());
        }
    }

    protected void storeYoutubeVideo(Video video) {
        DB.insertInto(new BhYoutubeVideoRecord(video.getId(), video.getTitle(), video.getChannelName(), video.getThumbnailUrl()));
    }

    protected List<Video> getUnknownVideos() {
        List<Video> uploadedVideos = getUploadedVideos();
        Collection<String> videoIds = uploadedVideos.stream().map(Video::getId).toList();

        List<String> knownVideos = DB.list(DB.createQuery()
                        .select()
                        .from(BH_YOUTUBE_VIDEO)
                        .where(BH_YOUTUBE_VIDEO.VIDEO_ID.in(videoIds)))
                .stream().map(r -> r.getValue(BH_YOUTUBE_VIDEO.VIDEO_ID)).toList();
        return uploadedVideos.stream().filter(video -> !knownVideos.contains(video.getId())).toList();
    }

    protected List<Video> getUploadedVideos() {
        try {
            PlaylistItemListResponse items = getService().playlistItems()
                    .list("id, snippet, contentDetails")
                    .setPlaylistId(CONFIG.get(YoutubeChannelUploadsPlaylistIdProperty.class))
                    .setMaxResults(CONFIG.get(YoutubeMaxPlaylistItemsProperty.class))
                    .execute();
            return items == null || items.getItems() == null ? Collections.emptyList() : items.getItems().stream().map(Video::new).toList();
        } catch (IOException | GeneralSecurityException e) {
            LOG.error("Error trying to fetch youtube videos!", e);
            return Collections.emptyList();
        }
    }

    protected YouTube getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Builder(httpTransport, JSON_FACTORY, request -> request.setInterceptor(intercepted -> intercepted.getUrl().set("key", CONFIG.get(YoutubeApiKeyProperty.class))))
                .setApplicationName(CONFIG.get(YoutubeBotNameProperty.class))
                .build();
    }

    @Override
    public void handleNotification(IComponentNotification notification) {
        // nop
    }

    public static class Video {

        private final PlaylistItem m_item;

        public Video(PlaylistItem item) {
            m_item = item;
        }

        public String getId() {
            return m_item.getId();
        }

        public String getVideoId() {
            return m_item.getContentDetails().getVideoId();
        }

        public String getTitle() {
            return m_item.getSnippet().getTitle();
        }

        public String getChannelName() {
            return m_item.getSnippet().getChannelTitle();
        }

        public String getThumbnailUrl() {
            return m_item.getSnippet().getThumbnails().getMaxres().getUrl();
        }

        public String getDescription() {
            return m_item.getSnippet().getDescription();
        }
    }
}
