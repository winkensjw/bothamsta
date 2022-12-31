package org.winkensjw.twitch.chatbot.chatrules.engine;

import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.winkensjw.twitch.TwitchChannelNames;

import java.util.Set;

public abstract class AbstractCommandChatRule extends AbstractChatMessageRule {

    public static final String COMMAND_CHARACTER = "!";

    public abstract Set<String> getKeyWords();

    public TwitchMessageEventHandler eventHandler() {
        return event -> sendMessage(event, getMessage(event));
    }

    protected abstract String getConfiguredMessage(ChannelMessageEvent event);

    protected String getMessage(ChannelMessageEvent event) {
        return isAtUser(event) ? atUser(getConfiguredMessage(event), event.getUser().getName())
                : getConfiguredMessage(event);
    }

    protected boolean isAtUser(ChannelMessageEvent event) {
        return true;
    }

    protected String atUser(String message, String userName) {
        return message + " @" + userName;
    }

    protected boolean matches(ChannelMessageEvent event) {
        return !isBothamsta(event) && isPermittedUser(event) && isThisChat(event) && messageMatchesKeywords(event);
    }

    protected boolean isPermittedUser(ChannelMessageEvent event) {
        // by default everyone can trigger the bot
        return true;
    }

    protected boolean isBothamsta(ChannelMessageEvent event) {
        return TwitchChannelNames.Bothamsta.equals(event.getUser().getName());
    }

    protected boolean messageMatchesKeywords(ChannelMessageEvent event) {
        return getKeyWords().stream().anyMatch(keyWord -> matchKeyWord(event, keyWord));
    }

    protected boolean matchKeyWord(ChannelMessageEvent event, String keyWord) {
        String message = event.getMessage().toLowerCase();
        String command = COMMAND_CHARACTER + keyWord;
        return message.equals(command) || message.startsWith(command + " ");
    }

    protected TwitchChat getChat(ChannelMessageEvent event) {
        return event.getTwitchChat();
    }

    protected boolean sendMessage(ChannelMessageEvent event, String message) {
        return getChat(event).sendMessage(event.getChannel().getName(), message);
    }
}
