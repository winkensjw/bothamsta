package org.winkensjw.platform.components;

import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class ComponentsRegistry {

    private static final Logger LOG = Logger.getLogger(ComponentsRegistry.class);
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

    public static void register(IComponent component) {
        try {
            getInstance().getRegistry().put(component.getClass().getName(), component);
            component.start();
        } catch (Exception e) {
            LOG.errorv(e, "Failed to start component: {0}", component.getClass());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends IComponent> T get(Class<T> componentClass) {
        return (T) getInstance().getRegistry().get(componentClass.getName());
    }

    public static void notifyComponents(IComponentNotification notification) {
        getInstance().getRegistry().values().forEach(component -> component.handleNotification(notification));
    }
}
