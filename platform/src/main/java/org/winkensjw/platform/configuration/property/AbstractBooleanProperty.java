package org.winkensjw.platform.configuration.property;

public abstract class AbstractBooleanProperty extends AbstractProperty<Boolean> {

    @Override
    public Class<Boolean> getTypeClass() {
        return Boolean.class;
    }

    @Override
    public Boolean getDefaultValue() {
        return false;
    }
}
