package org.winkensjw.platform.components;

import java.util.HashMap;
import java.util.Map;

public class ComponentsRegistry {

    private final Map<String, IComponent> m_registry = new HashMap<>();
    private static final ComponentsRegistry m_instance = new ComponentsRegistry();

    private ComponentsRegistry() {
    }

    public static ComponentsRegistry getInstance() {
        return m_instance;
    }

    protected Map<String, IComponent> getRegistry() {
        return m_registry;
    }

    public static void registerComponent(IComponent component) {
        getInstance().getRegistry().put(component.getClass().getName(), component);
        component.start();
    }

    @SuppressWarnings("unchecked")
    public static <T extends IComponent> T getComponent(Class<T> componentClass) {
        return (T) getInstance().getRegistry().get(componentClass.getName());
    }
}
