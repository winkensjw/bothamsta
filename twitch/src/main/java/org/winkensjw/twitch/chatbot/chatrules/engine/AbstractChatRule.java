package org.winkensjw.twitch.chatbot.chatrules.engine;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public abstract class AbstractChatRule {

    public abstract String getId();

    public abstract String getChannelName();

    protected boolean isThisChat(ChannelMessageEvent event) {
        return event.getChannel().getName().equals(getChannelName());
    }
}
