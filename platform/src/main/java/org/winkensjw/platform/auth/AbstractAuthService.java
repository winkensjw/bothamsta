package org.winkensjw.platform.auth;

import com.github.scribejava.core.model.OAuth2AccessToken;
import org.jboss.logging.Logger;
import org.jooq.Record;
import org.winkensjw.platform.db.DB;
import org.winkensjw.platform.db.schema.tables.records.BhOauthTokenRecord;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.winkensjw.platform.db.schema.Tables.BH_OAUTH_TOKEN;

public abstract class AbstractAuthService {
    private static final Logger LOG = Logger.getLogger(AbstractAuthService.class);

    public abstract String getAuthorizationUrl();

    public abstract String getServiceName();

    public abstract OAuth2AccessToken getAccessToken(String authCode);

    public abstract void handleAccessToken(OAuth2AccessToken accessToken);

    public abstract OAuth2AccessToken refreshAccessToken(String accountId, String refreshToken);


    public void authenticate(String accountId) {
        BhOauthTokenRecord refreshToken = fetchRefreshToken(getServiceName(), accountId);
        if (refreshToken == null) {
            openAuthorizationUrl(getAuthorizationUrl());
            return;
        }
        OAuth2AccessToken oAuth2AccessToken = refreshAccessToken(accountId, refreshToken.getRefreshToken());
        handleAccessToken(oAuth2AccessToken);
        storeRefreshToken(getServiceName(), accountId, oAuth2AccessToken.getRefreshToken());
    }

    public void openAuthorizationUrl(String authorizationUrl) {
        LOG.infov("Requesting auth code at {0}", authorizationUrl);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(authorizationUrl));
        } catch (IOException | URISyntaxException e) {
            LOG.error("Error trying to open auth code URL", e);
        }
    }

    public void processAuthCode(String authCode) {
        handleAccessToken(getAccessToken(authCode));
    }

    public void storeRefreshToken(String service, String accountId, String refreshToken) {
        Record record = fetchRefreshToken(service, accountId);
        if (record == null) {
            insertRefreshToken(service, accountId, refreshToken);
            return;
        }
        BhOauthTokenRecord oauthRecord = (BhOauthTokenRecord) record;
        oauthRecord.set(BH_OAUTH_TOKEN.REFRESH_TOKEN, refreshToken);
        DB.store(oauthRecord);
    }

    protected BhOauthTokenRecord fetchRefreshToken(String service, String accountId) {
        Record record = DB.uniqueResult(DB.createQuery()
                .select()
                .from(BH_OAUTH_TOKEN)
                .where(BH_OAUTH_TOKEN.SERVICE.eq(service).and(BH_OAUTH_TOKEN.ACCOUNT_ID.eq(accountId))));
        return record != null ? (BhOauthTokenRecord) record : null;
    }

    public void insertRefreshToken(String service, String accountId, String refreshToken) {
        BhOauthTokenRecord record = DB.newRecord(BH_OAUTH_TOKEN)
                .with(BH_OAUTH_TOKEN.OAUTH_TOKEN_ID, UUID.randomUUID().toString())
                .with(BH_OAUTH_TOKEN.SERVICE, service)
                .with(BH_OAUTH_TOKEN.ACCOUNT_ID, accountId)
                .with(BH_OAUTH_TOKEN.REFRESH_TOKEN, refreshToken);

        DB.insertInto(new BhOauthTokenRecord(UUID.randomUUID().toString(), service, accountId, refreshToken));
    }
}
