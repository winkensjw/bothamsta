package org.winkensjw.twitch.chatbot.chatrules.engine;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

@FunctionalInterface
public interface TwitchMessageEventHandler {

    boolean handle(ChannelMessageEvent messageEvent);
}
