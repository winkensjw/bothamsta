package org.winkensjw.platform.configuration.util;

import org.jboss.logging.Logger;
import org.winkensjw.platform.configuration.property.AbstractProperty;

import java.lang.reflect.InvocationTargetException;

public class CONFIG {

    private static final Logger LOG = Logger.getLogger(CONFIG.class);

    private CONFIG() {
        // no instance needed
    }

    public static <T> T get(Class<? extends AbstractProperty<T>> propertyClass) {
        AbstractProperty<T> property;
        try {
            property = propertyClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                 | NoSuchMethodException | SecurityException e) {
            LOG.error("Could not instantiate property for class: " + propertyClass, e);
            throw new RuntimeException(e); // rethrow
        }
        return property.getValue();
    }
}
