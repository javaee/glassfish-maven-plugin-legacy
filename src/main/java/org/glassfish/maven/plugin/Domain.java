/*******************************************************************************
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2007-2008 maven-glassfish-plugin developers. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by the copyright holder in the GPL Version 2 section of the
 * License file that accompanied this code.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 * by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 ******************************************************************************/

package org.glassfish.maven.plugin;

import au.net.ocean.maven.plugin.annotation.Parameter;
import au.net.ocean.maven.plugin.annotation.Required;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by dwhitla at Apr 3, 2007 8:45:45 PM
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: Domain.java 0 Apr 3, 2007 8:45:45 PM dwhitla $
 */
public class Domain {

    @Parameter(defaultValue = "localhost", description = "The target host")
    private String host = "localhost";

    @Parameter(
            expression = "${domainDirectory}",
            description = "The directory in which this domain should be created (if other than the Glassfish default).\n" +
            "Overrides the value of domainDirectory in global configuration.\n" +
            "This value is ignored if the host is other than localhost.")
    private File directory;

    @Parameter(expression = "${project.artifactId}")
    private String name;

    @Parameter(expression = "${domainDirectory}/${name}/log")
    private File logDirectory;

    @Required
    @Parameter
    private int adminPort;

    @Parameter
    private int httpPort;

    @Parameter
    private int httpsPort;

    @Parameter
    private int iiopPort;

    @Parameter
    private int iiopsPort;

    @Parameter
    private int iiopsmPort;

    @Parameter
    private int jmsPort;

    @Parameter(defaultValue = "false")
    private boolean reuse;

    @Parameter
    private Set<String> jvmOptions;

    @Parameter
    private Set<Property> properties;

    /**
     */
    private Auth auth;

    @Parameter(defaultValue = "developer")
    private String profile = "developer";

    @Parameter
    private String resourceDescriptor;

    @Parameter
    private Set<Resource> resources;

    @Parameter(description = "The admin password to use for this domain.")
    private String adminPassword;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getLogDirectory() {
        return logDirectory;
    }

    public void setLogDirectory(File logDirectory) {
        this.logDirectory = logDirectory;
    }

    public int getAdminPort() {
        return adminPort;
    }

    public void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }

    public int getHTTPPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getHTTPSPort() {
        return httpsPort;
    }

    public void setHttpsPort(int httpsPort) {
        this.httpsPort = httpsPort;
    }

    public int getIIOPPort() {
        return iiopPort;
    }

    public void setIiopPort(int iiopPort) {
        this.iiopPort = iiopPort;
    }

    public int getIiopsPort() {
        return iiopsPort;
    }

    public void setIiopsPort(int iiopsPort) {
        this.iiopsPort = iiopsPort;
    }

    public int getIiopsmPort() {
        return iiopsmPort;
    }

    public void setIiopsmPort(int iiopsmPort) {
        this.iiopsmPort = iiopsmPort;
    }

    public int getJMSPort() {
        return jmsPort;
    }

    public void setJmsPort(int jmsPort) {
        this.jmsPort = jmsPort;
    }

    public boolean isReuse() {
        return reuse;
    }

    public void setReuse(boolean reuse) {
        this.reuse = reuse;
    }

    public Set<String> getJVMOptions() {
        return jvmOptions;
    }

    public void setJvmOptions(Set<String> jvmOptions) {
        this.jvmOptions = jvmOptions;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(Set<Property> properties) {
        this.properties = properties;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getResourceDescriptor() {
        return resourceDescriptor;
    }

    public void setResourceDescriptor(String resourceDescriptor) {
        this.resourceDescriptor = resourceDescriptor;
    }
    
    public Set<Resource> getResources() {
        return resources;
    }

    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public boolean exists() {
        return ("localhost".equals(host))
                ? directory.exists() && Arrays.asList(directory.list()).contains(name)
                : false;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isStarted() {
        try {
            Socket socket = new Socket(host, adminPort);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }
    
}
