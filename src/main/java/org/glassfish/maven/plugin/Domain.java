/*******************************************************************************
 *
 * Copyright (c) 2007, Dave Whitla
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the copyright holder nor the names of contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
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
