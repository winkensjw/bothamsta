package org.winkensjw.twitter.auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/auth")
public class AuthCodeResource {

    @GET
    @Path("/code")
    @Produces(MediaType.APPLICATION_JSON)
    public void code(@QueryParam("code") String code) {
        new TwitterAuthenticator().processAuthCode(code);
    }
}
