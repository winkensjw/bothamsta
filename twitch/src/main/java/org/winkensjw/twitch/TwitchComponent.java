package org.winkensjw.twitch;

import org.jboss.logging.Logger;
import org.winkensjw.platform.components.IComponent;
import org.winkensjw.twitch.chatbot.TwitchBot;
import org.winkensjw.twitch.chatbot.chatrules.engine.ChatRuleInventory;

public class TwitchComponent implements IComponent {

    private static final Logger LOG = Logger.getLogger(TwitchComponent.class);

    @Override
    public void start() {
        LOG.info("Starting twitch component.");
        ChatRuleInventory.getInstance().initialize();
        TwitchBot bot = TwitchBot.getInstance();
        bot.joinChannel(TwitchChannelNames.Lufthamsta);
    }
}
