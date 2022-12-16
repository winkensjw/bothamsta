package org.winkensjw.twitter.auth;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import org.jboss.logging.Logger;
import org.winkensjw.platform.configuration.BothamstaServerProperties.TwitterClientIDProperty;
import org.winkensjw.platform.configuration.BothamstaServerProperties.TwitterClientSecretProperty;
import org.winkensjw.platform.configuration.util.CONFIG;
import org.winkensjw.twitter.TwitterComponent;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class TwitterAuthenticator {

    private static final Logger LOG = Logger.getLogger(TwitterAuthenticator.class);

    private final TwitterOAuth20Service m_service;

    public TwitterAuthenticator() {
        TwitterCredentialsOAuth2 credentials = new TwitterCredentialsOAuth2(CONFIG.get(TwitterClientIDProperty.class),
                CONFIG.get(TwitterClientSecretProperty.class),
                null,
                null);
        m_service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "http://localhost:8080/auth/code",
                "offline.access tweet.read users.read like.write");
    }

    public TwitterOAuth20Service getService() {
        return m_service;
    }

    public PKCE getPkce() {
        PKCE pkce = new PKCE();
        pkce.setCodeChallenge("challenge");
        pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
        pkce.setCodeVerifier("challenge");
        return pkce;
    }

    public void authenticate() {
        String authorizationUrl = getService().getAuthorizationUrl(getPkce(), "state");

        LOG.infov("Requesting auth code at {0}", authorizationUrl);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(authorizationUrl));
        } catch (IOException | URISyntaxException e) {
            LOG.error("Error trying to open auth code URL", e);
        }
    }

    public void processAuthCode(String authCode) {
        OAuth2AccessToken accessToken;
        try {
            accessToken = getService().getAccessToken(getPkce(), authCode);
        } catch (IOException | ExecutionException | InterruptedException e) {
            LOG.error("Error trying to get access token", e);
            return;
        }
        if (accessToken == null) {
            return;
        }
        TwitterCredentialsOAuth2 authToken = new TwitterCredentialsOAuth2(CONFIG.get(TwitterClientIDProperty.class),
                CONFIG.get(TwitterClientSecretProperty.class),
                accessToken.getAccessToken(),
                accessToken.getRefreshToken());

        Instance<TwitterComponent> twitterComponent = CDI.current().select(TwitterComponent.class);
        twitterComponent.get().setAuthToken(authToken);
    }

    public TwitterCredentialsOAuth2 refreshAccessToken(TwitterCredentialsOAuth2 token) {
        OAuth2AccessToken accessToken;
        try {
            accessToken = getService().refreshAccessToken(token.getTwitterOauth2RefreshToken());
            token.setTwitterOauth2AccessToken(accessToken.getAccessToken());
            token.setTwitterOauth2RefreshToken(accessToken.getRefreshToken());
            return token;
        } catch (IOException | InterruptedException | ExecutionException e) {
            LOG.error("Error trying to get refresh access token", e);
            throw new RuntimeException(e);
        }
    }
}
