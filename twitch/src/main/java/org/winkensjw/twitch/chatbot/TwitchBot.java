package org.winkensjw.twitch.chatbot;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.api.domain.IEventSubscription;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.jboss.logging.Logger;
import org.winkensjw.platform.configuration.BothamstaProperties.DebugProperty;
import org.winkensjw.platform.configuration.BothamstaProperties.TwitchOAuthTokenProperty;
import org.winkensjw.platform.configuration.util.CONFIG;
import org.winkensjw.twitch.chatbot.chatrules.engine.AbstractChatRule;
import org.winkensjw.twitch.chatbot.chatrules.engine.AbstractCommandChatRule;
import org.winkensjw.twitch.chatbot.chatrules.engine.AbstractTimedChatRule;
import org.winkensjw.twitch.chatbot.chatrules.engine.ChatRuleInventory;

import java.time.Duration;
import java.util.Set;

public class TwitchBot {

    private static final Logger LOG = Logger.getLogger(TwitchBot.class);

    private static final TwitchBot m_bot = new TwitchBot();
    private final TwitchClient m_twitchClient;

    private final Scheduler m_scheduler = new Scheduler();

    public Scheduler getScheduler() {
        return m_scheduler;
    }

    private TwitchBot() {
        // chat credential
        OAuth2Credential credential = new OAuth2Credential("twitch", CONFIG.get(TwitchOAuthTokenProperty.class));

        // twitch client
        m_twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credential)
                .build();

        // install debug handler
        if (CONFIG.get(DebugProperty.class)) {
            getTwitchClient().getEventManager().onEvent(ChannelMessageEvent.class, event -> LOG.info("[" + event.getChannel().getName() + "][" + event.getPermissions().toString() + "] "
                    + event.getUser().getName() + ": " + event.getMessage()));
        }
    }

    public static TwitchBot getInstance() {
        return m_bot;
    }

    protected TwitchClient getTwitchClient() {
        return m_twitchClient;
    }

    public void joinChannel(String channelName) {
        LOG.infov("Joining channel: {0}", channelName);
        getTwitchClient().getChat().joinChannel(channelName);
        registerRules(channelName);
        LOG.infov("Joined channel: {0}", channelName);
    }

    @SuppressWarnings("unused")
    public void leaveChannel(String channelName) {
        LOG.infov("Leaving channel: {0}", channelName);
        getTwitchClient().getChat().leaveChannel(channelName);
        unregisterRules(channelName);
        LOG.infov("Left channel: {0}", channelName);
    }

    protected void registerRules(String channelName) {
        getRulesForChannel(channelName).forEach(this::registerRule);
    }

    protected void unregisterRules(String channelName) {
        getRulesForChannel(channelName).forEach(this::unregisterRule);
    }

    protected Set<AbstractChatRule> getRulesForChannel(String channelName) {
        return ChatRuleInventory.getInstance().getChannelRules(channelName);
    }

    protected void registerRule(AbstractChatRule chatRule) {
        LOG.infov("Register rule: {0}", chatRule.getClass().getName());
        if (chatRule instanceof AbstractCommandChatRule commandRule) {
            registerCommandChatRule(commandRule);
        } else if (chatRule instanceof AbstractTimedChatRule timedRule) {
            registerTimedChatRule(timedRule);
        }
    }

    protected void registerCommandChatRule(AbstractCommandChatRule commandRule) {
        getTwitchClient().getEventManager().onEvent(commandRule.getId(), ChannelMessageEvent.class,
                commandRule::applyRule);
    }

    protected void registerTimedChatRule(AbstractTimedChatRule timedRule) {
        // subscribe to events to count messages
        getTwitchClient().getEventManager().onEvent(timedRule.getId(), ChannelMessageEvent.class,
                timedRule::countMessages);
        // periodically check the timed condition and send messages
        getScheduler().schedule(() -> timedRule.checkAndFire(getTwitchClient()), Schedules.fixedDelaySchedule(
                Duration.ofMinutes(timedRule.getTriggerTimeMinutes())));
    }

    protected void unregisterRule(AbstractChatRule chatRule) {
        LOG.infov("Unregister rule: {0}", chatRule.getClass().getName());
        getTwitchClient().getEventManager().getActiveSubscriptions().stream()
                .filter(sub -> chatRule.getId().equals(sub.getId())).forEach(IEventSubscription::dispose);
    }
}
