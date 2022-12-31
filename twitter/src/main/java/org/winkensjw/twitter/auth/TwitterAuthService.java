package org.winkensjw.twitter.auth;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.pkce.PKCE;
import com.github.scribejava.core.pkce.PKCECodeChallengeMethod;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.auth.TwitterOAuth20Service;
import org.jboss.logging.Logger;
import org.winkensjw.platform.auth.AbstractAuthService;
import org.winkensjw.platform.components.ComponentsRegistry;
import org.winkensjw.platform.configuration.BothamstaProperties.TwitterClientIDProperty;
import org.winkensjw.platform.configuration.BothamstaProperties.TwitterClientSecretProperty;
import org.winkensjw.platform.configuration.util.CONFIG;
import org.winkensjw.twitter.TwitterComponent;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TwitterAuthService extends AbstractAuthService {

    private static final Logger LOG = Logger.getLogger(TwitterAuthService.class);

    private final TwitterOAuth20Service m_service;

    public TwitterAuthService() {
        TwitterCredentialsOAuth2 credentials = new TwitterCredentialsOAuth2(CONFIG.get(TwitterClientIDProperty.class),
                CONFIG.get(TwitterClientSecretProperty.class),
                null,
                null);
        m_service = new TwitterOAuth20Service(
                credentials.getTwitterOauth2ClientId(),
                credentials.getTwitterOAuth2ClientSecret(),
                "http://localhost:8080/auth/code",
                "offline.access tweet.read tweet.write users.read like.write");
    }

    public TwitterOAuth20Service getService() {
        return m_service;
    }

    @Override
    public String getAuthorizationUrl() {
        return getService().getAuthorizationUrl(getPkce(), "state");
    }

    @Override
    public String getServiceName() {
        return "twitter";
    }

    public PKCE getPkce() {
        PKCE pkce = new PKCE();
        pkce.setCodeChallenge("challenge");
        pkce.setCodeChallengeMethod(PKCECodeChallengeMethod.PLAIN);
        pkce.setCodeVerifier("challenge");
        return pkce;
    }


    public TwitterCredentialsOAuth2 createToken(String accessToken, String refreshToken) {
        return new TwitterCredentialsOAuth2(CONFIG.get(TwitterClientIDProperty.class),
                CONFIG.get(TwitterClientSecretProperty.class),
                accessToken,
                refreshToken);
    }

    @Override
    public OAuth2AccessToken getAccessToken(String authCode) {
        try {
            return getService().getAccessToken(getPkce(), authCode);
        } catch (IOException | ExecutionException | InterruptedException e) {
            LOG.error("Error trying to get access token", e);
            return null;
        }
    }

    public OAuth2AccessToken refreshAccessToken(String accountId, String refreshToken) {
        try {
            return getService().refreshAccessToken(refreshToken);
        } catch (IOException | InterruptedException | ExecutionException e) {
            LOG.error("Error trying to get refresh access token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleAccessToken(OAuth2AccessToken accessToken) {
        if (accessToken == null) {
            return;
        }
        TwitterCredentialsOAuth2 authToken = createToken(accessToken.getAccessToken(), accessToken.getRefreshToken());
        // FIXME JWI use notifications
        TwitterComponent component = ComponentsRegistry.get(TwitterComponent.class);
        component.setAuthToken(authToken);
    }

}
