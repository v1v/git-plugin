package hudson.plugins.git.extensions.impl;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.plugins.git.GitException;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.GitClientType;
import hudson.plugins.git.extensions.GitSCMExtension;
import hudson.plugins.git.extensions.GitSCMExtensionDescriptor;

import org.jenkinsci.plugins.gitclient.CloneCommand;
import org.jenkinsci.plugins.gitclient.FetchCommand;
import org.jenkinsci.plugins.gitclient.GitClient;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public class CloneOption extends GitSCMExtension {
    private boolean shallow;
    private String reference;
    private Integer timeout;
    private Integer depth = 1;

    @DataBoundConstructor
    public CloneOption(boolean shallow, String reference, Integer timeout, Integer depth) {
        this.shallow = shallow;
        this.reference = reference;
        this.timeout = timeout;
        if (depth != null) {
          this.depth = depth;
        }
    }

    public boolean isShallow() {
        return shallow;
    }

    public String getReference() {
        return reference;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public Integer getDepth() {
        return depth;
    }

    @Override
    public void decorateCloneCommand(GitSCM scm, Run<?, ?> build, GitClient git, TaskListener listener, CloneCommand cmd) throws IOException, InterruptedException, GitException {
        if (shallow) {
            listener.getLogger().println("Using shallow clone");
            cmd.shallow();
            cmd.depth(depth);
        }
        cmd.timeout(timeout);
        cmd.reference(build.getEnvironment(listener).expand(reference));
    }

    @Override
    public void decorateFetchCommand(GitSCM scm, GitClient git, TaskListener listener, FetchCommand cmd) throws IOException, InterruptedException, GitException {
    	cmd.timeout(timeout);
    }

    @Override
    public GitClientType getRequiredClient() {
        return GitClientType.GITCLI;
    }

    @Extension
    public static class DescriptorImpl extends GitSCMExtensionDescriptor {
        @Override
        public String getDisplayName() {
            return "Advanced clone behaviours";
        }
    }

}
