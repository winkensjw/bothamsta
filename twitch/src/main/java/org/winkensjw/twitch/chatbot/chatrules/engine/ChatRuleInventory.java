package org.winkensjw.twitch.chatbot.chatrules.engine;

import org.winkensjw.twitch.TwitchChannelNames;
import org.winkensjw.twitch.chatbot.chatrules.lufthamsta.LufthamstaChannelRules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class ChatRuleInventory {

    private static final ChatRuleInventory m_instance = new ChatRuleInventory();
    private Map<String, Class<? extends ChatRuleProvider>> m_inventory = new HashMap<>();

    private ChatRuleInventory() {
    }

    public static ChatRuleInventory getInstance() {
        return m_instance;
    }

    public void initialize() {
        m_inventory.put(TwitchChannelNames.Lufthamsta, LufthamstaChannelRules.class);
    }

    public Set<AbstractChatRule> getChannelRules(String channelName) {
        Class<? extends ChatRuleProvider> providerClass = m_inventory.get(channelName);
        if (providerClass != null) {
            return provideRules(providerClass);
        }
        return Collections.emptySet();
    }

    protected Set<AbstractChatRule> provideRules(Class<? extends ChatRuleProvider> providerClass) {
        Class<?>[] declaredRules = providerClass.getDeclaredClasses();
        Set<AbstractChatRule> rules = new HashSet<>();
        Stream.of(declaredRules)
                .filter(rule -> !rule.isInterface() && !Modifier.isAbstract(rule.getModifiers()))
                .forEach(rule -> {
                    try {
                        rules.add((AbstractChatRule) rule.getDeclaredConstructor().newInstance());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                    }
                });
        return rules;
    }
}
