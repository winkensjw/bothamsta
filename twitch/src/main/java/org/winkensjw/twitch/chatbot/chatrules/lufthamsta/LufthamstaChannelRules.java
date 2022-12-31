package org.winkensjw.twitch.chatbot.chatrules.lufthamsta;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import org.winkensjw.platform.util.StringUtility;
import org.winkensjw.twitch.TwitchChannelNames;
import org.winkensjw.twitch.chatbot.chatrules.engine.AbstractCommandChatRule;
import org.winkensjw.twitch.chatbot.chatrules.engine.AbstractTimedChatRule;
import org.winkensjw.twitch.chatbot.chatrules.engine.ChatRuleProvider;

import java.util.HashSet;
import java.util.Set;

public final class LufthamstaChannelRules implements ChatRuleProvider {

    private LufthamstaChannelRules() {
    }

    public static abstract class AbstractLufthamstaCommandRule extends AbstractCommandChatRule {

        private String m_id;
        private Set<String> m_keyWords;

        public String getChannelName() {
            return TwitchChannelNames.Lufthamsta;
        }

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
            return StringUtility.join("-", TwitchChannelNames.Lufthamsta, m_id, "command");
        }

        @Override
        public Set<String> getKeyWords() {
            return m_keyWords;
        }
    }

    public static abstract class AbstractLufthamstaTimedChatRule extends AbstractTimedChatRule {
        private String m_id;
        private int m_minimumMessagesPassed;
        private int m_triggerTimeMinutes;

        public AbstractLufthamstaTimedChatRule(String id, int minimumMessagesPassed, int triggerTimeMinutes) {
            m_id = id;
            m_minimumMessagesPassed = minimumMessagesPassed;
            m_triggerTimeMinutes = triggerTimeMinutes;
        }

        public int getTriggerTimeMinutes() {
            return m_triggerTimeMinutes;
        }

        public int getMinimumMessagesPassed() {
            return m_minimumMessagesPassed;
        }

        public String getChannelName() {
            return TwitchChannelNames.Lufthamsta;
        }

        @Override
        public String getId() {
            return StringUtility.join("-", TwitchChannelNames.Lufthamsta, m_id, "timed");
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
            return "Du möchtest Emotes sehen/nutzen? Dann installier dir die 7TV Browser-Extension: https://7tv.app/ pepeMoney ICANT GIGACHAD VIBE";
        }
    }

    public static class EmotesTimedRule extends AbstractLufthamstaTimedChatRule {

        public EmotesTimedRule() {
            super("emotes", -1, 90);
        }

        @Override
        protected String getConfiguredMessage() {
            return "Du möchtest Emotes sehen/nutzen? Dann installier dir die 7TV Browser-Extension: https://7tv.app/ pepeMoney ICANT GIGACHAD VIBE";
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

    public static class TimedFollowRule extends AbstractLufthamstaTimedChatRule {

        public TimedFollowRule() {
            super("follow", 5, 30);
        }


        @Override
        protected String getConfiguredMessage() {
            return "Folg mir doch in dem du einfach auf das Herz klickts. Kost nix.";
        }
    }
}
