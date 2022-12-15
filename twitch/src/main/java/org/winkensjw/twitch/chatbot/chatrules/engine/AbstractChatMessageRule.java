package org.winkensjw.twitch.chatbot.chatrules.engine;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

public abstract class AbstractChatMessageRule extends AbstractChatRule {

    protected abstract TwitchMessageEventHandler eventHandler();

    protected abstract boolean matches(ChannelMessageEvent event);

    public boolean applyRule(ChannelMessageEvent event) {
        if (matches(event)) {
            return eventHandler().handle(event);
        }
        return false;
    }
}
