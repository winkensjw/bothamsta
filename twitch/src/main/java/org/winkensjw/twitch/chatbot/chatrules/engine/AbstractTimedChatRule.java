package org.winkensjw.twitch.chatbot.chatrules.engine;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.jboss.logging.Logger;
import org.winkensjw.twitch.TwitchChannelNames;

public abstract class AbstractTimedChatRule extends AbstractChatRule {
    private static final Logger LOG = Logger.getLogger(AbstractTimedChatRule.class);
    private int m_messagesPassed = 0;

    public abstract int getTriggerTimeMinutes();

    public abstract int getMinimumMessagesPassed();


    protected abstract String getConfiguredMessage();

    public void countMessages(ChannelMessageEvent event) {
        if (!isThisChat(event)
                || event.getUser().getName().equals(TwitchChannelNames.Lufthamsta)
                || event.getUser().getName().equals(TwitchChannelNames.Bothamsta)) {
            return;
        }
        m_messagesPassed++;
    }

    protected boolean sendMessage(TwitchClient twitchClient, String message) {
        return twitchClient.getChat().sendMessage(getChannelName(), message);
    }

    public void checkAndFire(TwitchClient twitchClient) {
        LOG.infov("Checking rule {0} for channel {1}. Current message count: {2}", getClass().getSimpleName(), getChannelName(), m_messagesPassed);
        if (m_messagesPassed < getMinimumMessagesPassed()) {
            return;
        }
        sendMessage(twitchClient, getConfiguredMessage());
        m_messagesPassed = 0;
    }
}
