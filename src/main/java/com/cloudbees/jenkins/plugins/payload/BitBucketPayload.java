package com.cloudbees.jenkins.plugins.payload;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;
import hudson.model.InvisibleAction;
import net.sf.json.JSONObject;

import javax.annotation.Nonnull;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Inject the payload received by BitBucket into the build through $BITBUCKET_PAYLOAD so it can be processed
 * @since January 9, 2016
 * @version 1.1.5
 */
public class BitBucketPayload extends InvisibleAction implements EnvironmentContributingAction {
    protected final @Nonnull JSONObject payload;
    private String scm;
    private String user;
    private String scmUrl;

    public BitBucketPayload(@Nonnull JSONObject payload) {
        this.payload = payload;
        JSONObject repository = payload.getJSONObject("repository");
        JSONObject actor = payload.getJSONObject("actor");

        this.user = actor.getString("username");
        this.scm = repository.has("scm") ? repository.getString("scm") : "git";
        this.scmUrl = repository.getJSONObject("links").getJSONObject("html").getString("href");
    }

    @Nonnull
    public JSONObject getPayload() {
        return payload;
    }

    public String getScm() {
        return scm;
    }

    public String getUser() {
        return user;
    }

    public String getScmUrl() {
        return scmUrl;
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> abstractBuild, EnvVars envVars) {
        envVars.put("BITBUCKET_PAYLOAD", payload.toString());
        LOGGER.log(Level.FINEST, "Injecting BITBUCKET_PAYLOAD: {0}", payload);
    }

    private static final Logger LOGGER = Logger.getLogger(BitBucketPayload.class.getName());
}
