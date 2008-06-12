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

import au.net.ocean.maven.plugin.MojoConfigurationException;
import au.net.ocean.maven.plugin.OceanMojo;
import au.net.ocean.maven.plugin.annotation.Parameter;
import au.net.ocean.maven.plugin.annotation.Required;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by dwhitla at Apr 2, 2007 6:59:36 PM
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: GlassFishMojo.java 0 Apr 2, 2007 6:59:36 PM dwhitla $
 */
public abstract class GlassfishMojo extends OceanMojo {
    
    @Parameter(description = "The directory into which domains are deployed (defaults to ${glassfishDirectory}/domains).")
    protected File domainDirectory;

    @Parameter(description = "Container for domain configuration parameters.")
    protected Domain domain;

    @Parameter(description = "Peer domain fixtures for testing a JEE component againts other existing remote JEE components " +
            "which can be deployed remote domains, created during the automated build.")
    protected List<Domain> testing;

    @Required
    @Parameter(description = "The root directory of the Glassfish installation to be used", expression = "${glassfish.home}")
    private File glassfishDirectory;

    @Parameter(description = "Debug Glassfish output", defaultValue = "false")
    private boolean debug;

    @Parameter(description = "Echo Glassfish asadmin commands", defaultValue = "false")
    private boolean echo;

    @Parameter(description = "Terse Glassfish output", defaultValue = "true")
    private boolean terse = true;

    @Parameter(description = "Skip execution", defaultValue = "false")
    private boolean skip;

    @Parameter(description = "Automatically create the domain if it does not already exist", defaultValue = "true")
    private boolean autoCreate;

    @Parameter(description = "Glassfish user", expression = "${user.name}")
    private String user;

    @Parameter(description = "Location of the admin password file.")
    private String passFile;

    @Parameter(description = "The admin password to use for this domain.")
    private String adminPassword;

    protected String getPrefix() {
        return "glassfish";
    }

    public File getGlassfishDirectory() {
        return glassfishDirectory;
    }

    public void setGlassfishDirectory(File glassfishDirectory) {
        this.glassfishDirectory = glassfishDirectory;
    }

    public File getDomainDirectory() {
        return domainDirectory;
    }

    public void setDomainDirectory(File domainDirectory) {
        this.domainDirectory = domainDirectory;
    }

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isEcho() {
        return echo;
    }

    public void setEcho(boolean echo) {
        this.echo = echo;
    }

    public boolean isTerse() {
        return terse;
    }

    public void setTerse(boolean terse) {
        this.terse = terse;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getPassFile() {
        return passFile;
    }

    public void setPassFile(String passFile) {
        this.passFile = passFile;
    }

    protected void postConfig() throws MojoConfigurationException {
        List<String> configErrors = getConfigErrors();
        if (!configErrors.isEmpty()) {
            throw new MojoConfigurationException(this, configErrors);
        }
        if (adminPassword != null && adminPassword.length() > 0) {
            // create temporary passfile
            try {
                File tmpPassFile = File.createTempFile("mgfp", null);
                tmpPassFile.deleteOnExit();
                passFile = tmpPassFile.getAbsolutePath();
                PrintWriter fileWriter = new PrintWriter(new FileWriter(tmpPassFile));
                fileWriter.println("AS_ADMIN_PASSWORD=" + adminPassword);
                fileWriter.println("AS_ADMIN_USERPASSWORD=" + adminPassword);
                fileWriter.println("AS_ADMIN_ADMINPASSWORD=" + adminPassword);
                fileWriter.println("AS_ADMIN_MASTERPASSWORD=" + adminPassword);
                fileWriter.close();
            } catch (IOException e) {
                throw new MojoConfigurationException(this,
                        Arrays.asList("Unable to create temporary asadmin password file in " + System.getProperty("java.io.tmpdir")));
            }
        }
        if (domainDirectory == null) {
            domainDirectory = new File(glassfishDirectory, "domains");
        }
//        for (Domain domain : domains) {
            if (domain.getDirectory() == null) domain.setDirectory(domainDirectory);
//        }
    }

    protected List<String> getConfigErrors() {
        List<String> errors = new ArrayList<String>();
        // adminPort or basePort are required
        // passfile or adminPassword are required
        if (adminPassword == null && passFile == null) {
            StringBuilder error = new StringBuilder()
                    .append("inside the definition for plugin: 'maven-glassfish-plugin' specify the following:\n\n")
                    .append("<configuration>\n")
                    .append("  ...\n")
                    .append("  <passFile>VALUE</passFile>\n")
                    .append("  ...\n")
                    .append("   OR\n")
                    .append("  ...\n")
                    .append("  <adminPassword>VALUE</adminPassword>\n")
                    .append("  ...\n")
                    .append("</configuration>\n");
            errors.add(error.toString());
        }
        return errors;
    }


    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }
}
