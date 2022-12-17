package org.winkensjw.platform.components;

public interface IComponent {

    void start();

    void handleNotification(IComponentNotification notification);
}
