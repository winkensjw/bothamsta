package org.winkensjw.platform.configuration.property;

public abstract class AbstractStringProperty extends AbstractProperty<String> {

    @Override
    public Class<String> getTypeClass() {
        return String.class;
    }
}
