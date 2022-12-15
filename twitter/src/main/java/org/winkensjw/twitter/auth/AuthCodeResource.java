package org.winkensjw.twitter.auth;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/auth")
public class AuthCodeResource {

    private static final Logger LOG = Logger.getLogger(AuthCodeResource.class);

    @GET
    @Path("/code")
    @Produces(MediaType.APPLICATION_JSON)
    public void code(@QueryParam("code") String code) {
        LOG.infov("Received auth code: {0}", code);
        new TwitterAuthenticator().processAuthCode(code);
    }
}
