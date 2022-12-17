package org.winkensjw.platform.components;

public class TwitterCreateTweetComponentNotification extends TwitterComponentNotification {

    private final String m_text;
    private final String m_imgUrl;

    public TwitterCreateTweetComponentNotification(String text, String imgUrl) {
        m_text = text;
        m_imgUrl = imgUrl;
    }

    public String getText() {
        return m_text;
    }

    public String getImgUrl() {
        return m_imgUrl;
    }
}
