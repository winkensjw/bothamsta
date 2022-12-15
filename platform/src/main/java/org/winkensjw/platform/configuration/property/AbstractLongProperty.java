package org.winkensjw.platform.configuration.property;

public abstract class AbstractLongProperty extends AbstractProperty<Long> {

    @Override
    public Class<Long> getTypeClass() {
        return Long.class;
    }

    @Override
    public Long getDefaultValue() {
        return 0L;
    }
}
