package org.winkensjw.platform.configuration.property;

import org.eclipse.microprofile.config.ConfigProvider;

public abstract class AbstractProperty<T> {

    public abstract String getId();

    public abstract Class<T> getTypeClass();

    public T getDefaultValue() {
        return null;
    }

    public T getValue() {
        T val = ConfigProvider.getConfig().getValue(getId(), getTypeClass());
        return val == null ? getDefaultValue() : val;
    }
}
