package org.winkensjw.twitch.chatbot.chatrules.lufthamsta;

import org.winkensjw.twitch.TwitchChannelNames;
import org.winkensjw.twitch.chatbot.chatrules.engine.AbstractCommandChatRule;
import org.winkensjw.twitch.chatbot.chatrules.engine.ChatRuleProvider;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import java.util.HashSet;
import java.util.Set;

public final class LufthamstaChannelRules implements ChatRuleProvider {

    private LufthamstaChannelRules() {
    }

    public static abstract class AbstractLufthamstaCommandRule extends AbstractCommandChatRule {

        private String m_id;
        private Set<String> m_keyWords;

        public AbstractLufthamstaCommandRule(String id) {
            this(id, id);
        }

        public AbstractLufthamstaCommandRule(String id, String... keyWords) {
            m_id = id;
            m_keyWords = new HashSet<>();
            m_keyWords.addAll(Set.of(keyWords));
            m_keyWords.add(id);
        }

        @Override
        protected boolean isAtUser(ChannelMessageEvent event) {
            return !TwitchChannelNames.Lufthamsta.equals(event.getUser().getName());
        }

        @Override
        public String getId() {
            return TwitchChannelNames.Lufthamsta + "-" + m_id;
        }

        @Override
        public Set<String> getKeyWords() {
            return m_keyWords;
        }

    }

    public static class BsgRule extends AbstractLufthamstaCommandRule {

        public BsgRule() {
            super("bsg");
        }

        @Override
        protected String getConfiguredMessage(ChannelMessageEvent event) {
            return "Hier ist Backseat Gaming in gewissem Rahmen erlaubt. Helft mir, aber spoilert keine Handlung/Jumpscares etc. Sonst wird's lahm.";
        }
    }

    public static class DiscordRule extends AbstractLufthamstaCommandRule {

        public DiscordRule() {
            super("discord", "dc");
        }

        @Override
        protected String getConfiguredMessage(ChannelMessageEvent event) {
            return "Werde hier Teil der Hamstabande und verpasse keinen Stream mehr! https://discord.gg/3xu4pDM5qf";
        }
    }

    public static class EmotesRule extends AbstractLufthamstaCommandRule {

        public EmotesRule() {
            super("emotes");
        }

        @Override
        protected String getConfiguredMessage(ChannelMessageEvent event) {
            return "Du möchtest Lufthamsta Emotes nutzen? Dann installier dir FrankerFaceZ: https://www.frankerfacez.com/";
        }
    }

    public static class FreundesCodeRule extends AbstractLufthamstaCommandRule {

        public FreundesCodeRule() {
            super("fc", "nintendo", "friendcode", "freundescode");
        }

        @Override
        protected String getConfiguredMessage(ChannelMessageEvent event) {
            return "Freundescode: SW-6970-0587-4597";
        }
    }

    public static class FollowRule extends AbstractLufthamstaCommandRule {

        public FollowRule() {
            super("follow");
        }

        @Override
        protected String getConfiguredMessage(ChannelMessageEvent event) {
            return "Folg mir doch in dem du einfach auf das Herz klickts. Kost nix.";
        }
    }
}
