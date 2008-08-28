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
    
    @Parameter(description = "The directory into which domains are deployed. Default value is ${glassfishDirectory}/domains.")
    protected File domainDirectory;

    @Required
    @Parameter(description = "Container for domain configuration parameters.")
    protected Domain domain;

//    @Parameter(description = "Peer domain fixtures for testing a JEE component against other existing remote JEE components " +
//            "which can be deployed remote domains, created during the automated build.")
//    protected List<Domain> testing;

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

    @Parameter(description = "The unprivileged user to run as", expression = "${user.name}")
    private String user;

    @Parameter(
            description = "Location of the asadmin style password file (if you do not want to provide the password in your POM)")
    private String passwordFile;

    @Parameter(
            description = "The admin password to use for this domain (if you would rather not use an asadmin style password file)"
    )
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

    public String getPasswordFile() {
        return passwordFile;
    }

    public void setPasswordFile(String passwordFile) {
        this.passwordFile = passwordFile;
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
                passwordFile = tmpPassFile.getAbsolutePath();
                PrintWriter fileWriter = new PrintWriter(new FileWriter(tmpPassFile));
                fileWriter.println("AS_ADMIN_PASSWORD=" + adminPassword);
                fileWriter.println("AS_ADMIN_USERPASSWORD=" + adminPassword);
                fileWriter.println("AS_ADMIN_ADMINPASSWORD=" + adminPassword);
                fileWriter.println("AS_ADMIN_MASTERPASSWORD=" + adminPassword);
                fileWriter.close();
            } catch (IOException e) {
                throw new MojoConfigurationException(this,
                        Arrays.asList("Unable to create temporary asadmin password file in "
                                + System.getProperty("java.io.tmpdir")));
            }
        }
        // todo: this should be left to asadmin to decide
        if (domainDirectory == null) {
            domainDirectory = new File(glassfishDirectory, "domains");
        }
//        for (Domain domain : domains) {
            if (domain.getDirectory() == null) {
                domain.setDirectory(domainDirectory);
            }
//        }
    }

    protected List<String> getConfigErrors() {
        List<String> errors = new ArrayList<String>();
        // adminPort or basePort are required
        // passfile or adminPassword are required
        if (adminPassword == null && passwordFile == null) {
            StringBuilder error = new StringBuilder()
                    .append("inside the definition for plugin: 'maven-glassfish-plugin' specify the following:\n\n")
                    .append("<configuration>\n")
                    .append("  ...\n")
                    .append("  <passwordFile>VALUE</passwordFile>\n")
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
