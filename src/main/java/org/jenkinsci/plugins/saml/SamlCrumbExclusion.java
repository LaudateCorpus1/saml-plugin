package org.jenkinsci.plugins.saml;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hudson.Extension;
import hudson.security.csrf.CrumbExclusion;

/**
 * @see hudson.security.csrf.CrumbExclusion
 */
@Extension
public class SamlCrumbExclusion extends CrumbExclusion {
    private static final Logger LOG = Logger.getLogger(SamlCrumbExclusion.class.getName());

    @Override
    public boolean process(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        jenkins.model.Jenkins j = jenkins.model.Jenkins.get();
        if (j.getSecurityRealm() instanceof SamlSecurityRealm
            && shouldExclude(request.getPathInfo())) {
            chain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private static boolean shouldExclude(String pathInfo) {
        if (pathInfo == null) {
            LOG.fine("SamlCrumbExclusion.shouldExclude empty");
            return false;
        }
        if (pathInfo.startsWith("/" + SamlSecurityRealm.CONSUMER_SERVICE_URL_PATH)) {
            LOG.fine("SamlCrumbExclusion.shouldExclude excluding '" + pathInfo + "'");
            return true;
        } else {
            LOG.finer("SamlCrumbExclusion.shouldExclude keeping '" + pathInfo + "'");
            return false;
        }
    }
}
