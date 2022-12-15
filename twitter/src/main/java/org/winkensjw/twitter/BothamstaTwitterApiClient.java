package org.winkensjw.twitter;

import com.twitter.clientlib.ApiClient;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.JSON;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.File;

public class BothamstaTwitterApiClient extends ApiClient {

    @Override
    public RequestBody serialize(Object obj, String contentType) throws ApiException {
        if (obj instanceof byte[]) {
            // Binary (byte array) body parameter support.
            return RequestBody.create(MediaType.parse(contentType), (byte[]) obj);
        } else if (obj instanceof File) {
            // File body parameter support.
            return RequestBody.create(MediaType.parse(contentType), (File) obj);
        } else if ("text/plain".equals(contentType) && obj instanceof String) {
            return RequestBody.create(MediaType.parse(contentType), (String) obj);
        } else if (isJsonMime(contentType)) {
            String content;
            if (obj != null) {
                content = JSON.serialize(obj);
            } else {
                content = null;
            }
            return RequestBody.create(MediaType.parse(contentType), content);
        } else {
            throw new ApiException("Content type \"" + contentType + "\" is not supported");
        }
    }
}
