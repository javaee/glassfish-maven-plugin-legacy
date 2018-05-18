/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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
 */

package org.glassfish.maven.plugin.command;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.glassfish.maven.plugin.GlassfishMojo;
import org.glassfish.maven.plugin.Property;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by dwhitla at Apr 9, 2007 3:39:26 PM
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: Command.java 0 Apr 9, 2007 3:39:26 PM dwhitla $
 */
public abstract class AsadminCommand {

    private static final int PROCESS_LOOP_SLEEP_MILLIS = 100;
    private static final int EXIT_SUCCESS = 0;
    protected GlassfishMojo sharedContext;
    private InputStream processOut;
    private InputStream processErr;

    protected AsadminCommand(GlassfishMojo sharedContext) {
        this.sharedContext = sharedContext;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        execute(new ProcessBuilder());
    }

    public void execute(ProcessBuilder processBuilder) throws MojoExecutionException, MojoFailureException {
        List<String> commandLine = new ArrayList<String>(getParameters());
        File binDir = new File(sharedContext.getGlassfishDirectory(), "bin");
        File asadmin = new File(binDir, "asadmin");

        // bnevins 9/13/11 -- the install may have both asadmin and asadmin.bat
        // if we are on Windows - then prefer the .bat file and explicitly set it.
        // o/w windows will attempt running the UNIX script which is trouble!
        // http://java.net/jira/browse/MAVEN_GLASSFISH_PLUGIN-5
        if (System.getProperty("os.name").contains("indows")) {
            File asadminBat = new File(binDir, "asadmin.bat");
            if (asadminBat.exists()) {
                asadmin = asadminBat;
            }
        }

        commandLine.addAll(0, Arrays.asList(
                asadmin.getAbsolutePath(),
                getName(),
                "--echo=" + sharedContext.isEcho(),
                "--terse=" + sharedContext.isTerse()
        ));

        Log log = sharedContext.getLog();
        log.debug(commandLine.toString());
        processBuilder.command(commandLine);
        try {
            int exitValue;
            Process process = processBuilder.start();
            processOut = process.getInputStream();
            processErr = process.getErrorStream();
            BufferedReader outReader = new BufferedReader(new InputStreamReader(processOut));
            do {
                try {
                    exitValue = process.exitValue();
                    break;
                } catch (IllegalThreadStateException e) {
                    Thread.sleep(PROCESS_LOOP_SLEEP_MILLIS);
                } finally {
                    while (outReader.ready()) {
                        log.info(outReader.readLine());
                    }
                }
            } while(true);
            if (exitValue != EXIT_SUCCESS) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(processErr));
                while (errorReader.ready()) {
                    log.error(errorReader.readLine());
                }
                String errorMessage = getErrorMessage();
                log.error(errorMessage);
                log.error("For more detail on what might be causing the problem try running maven with the --debug option ");
                log.error("or setting the maven-glassfish-plugin \"echo\" property to \"true\".");
                throw new MojoFailureException(errorMessage);
            }
        } catch (IOException e) {
            throw new MojoExecutionException(getErrorMessage() + " IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new MojoExecutionException(getErrorMessage() + " Process was interrupted: " + e.getMessage());
        }
    }

    protected String escape(String value, String chars) {
        return escape(value, chars, "\\\\");
    }

    protected String escape(String value, String chars, String escapeSequence) {
        String escaped = value;
        if (escaped == null) {
            return "";
        }
        for (char ch : chars.toCharArray()) {
            escaped = escaped.replaceAll(String.valueOf(ch), escapeSequence + ch);
        }
        return escaped;
    }

    protected abstract String getName();

    protected abstract List<String> getParameters();

    protected abstract String getErrorMessage();

    public InputStream getOut() {
        return processOut;
    }

    public InputStream getErr() {
        return processErr;
    }

    protected void addProperties(List<String> commandLine, Set<Property> objectProperties) {
        if (objectProperties != null && !objectProperties.isEmpty()) {
            StringBuilder properties = new StringBuilder();
            for (Property property : objectProperties) {
                if (properties.length() > 0) {
                    properties.append(':');
                }
                String name = escape(property.getName(), "=;:");
                String value = escape(property.getValue(), "=;:");
                properties.append(name).append('=').append(value);
            }
            commandLine.add("--property");
            commandLine.add(properties.toString());
        }
    }

    protected void addOptionalParameter(List<String> commandLine, String parameterName, String parameterValue) {
        if (parameterValue != null && parameterValue.trim().length() > 0) {
            commandLine.add(parameterName);
            commandLine.add(parameterValue);
        }
    }

    protected void addOptionalParameter(List<String> commandLine, String parameterName, int parameterValue) {
        if (parameterValue > 0) {
            commandLine.add(parameterName);
            commandLine.add(String.valueOf(parameterValue));
        }
    }

    protected void addOptionalParameter(List<String> commandLine, String parameterName, File parameterValue) {
        if (parameterValue != null) {
            commandLine.add(parameterName);
            commandLine.add(parameterValue.getAbsolutePath());
        }
    }

    protected void addSwitch(List<String> commandLine, String name, boolean value) {
        if (value) {
            commandLine.add(name);
        }
    }
}

