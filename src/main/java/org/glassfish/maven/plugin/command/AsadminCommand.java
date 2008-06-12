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

package org.glassfish.maven.plugin.command;

import org.glassfish.maven.plugin.GlassfishMojo;
import org.glassfish.maven.plugin.Property;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

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

    private static final int EXIT_SUCCESS = 0;
    private InputStream processOut;
    private InputStream processErr;
    protected GlassfishMojo sharedContext;

    protected AsadminCommand(GlassfishMojo sharedContext) {
        this.sharedContext = sharedContext;
    }

    public void execute() throws MojoExecutionException {
        execute(new ProcessBuilder());
    }

    public void execute(ProcessBuilder processBuilder) throws MojoExecutionException {
        List<String> commandLine = new ArrayList<String>(getParameters());
        commandLine.addAll(0, Arrays.asList(
                sharedContext.getGlassfishDirectory().getAbsolutePath() + "/bin/asadmin",
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
                    Thread.sleep(100);
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
                throw new MojoExecutionException(getErrorMessage() + " See above for details.");
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
        for (char ch : chars.toCharArray()) {
            value = value.replaceAll(String.valueOf(ch), escapeSequence + ch);
        }
        return value;
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

