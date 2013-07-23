/*
 * (c) Copyright 2013 - The gf-maven-plugin developers. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or license/LICENSE.html.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at license/LICENSE.html.
 * This file is subject to the "Classpath" exception as provided by the 
 * copyright holder in the GPL Version 2 section of the
 * License file that accompanied this code.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 * by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */
package org.glassfish.maven.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

/* All goals extend this class */
public abstract class GlassFishMojo extends AbstractMojo {
    
    /* Hard-code the GlassFish command-line tools */
    public enum Command {
        APPCLIENT("appclient"),
        ASADMIN("asadmin"),
        CAPTURE_SCHEMA("capture-schema"),
        JSPC("jspc"),
        PACKAGE_APPCLIENT("package-appclient"),
        SCHEMAGEN("schemagen"),
        WSCOMPILE("wscompile"),
        WSDEPLOY("wsdeploy"),
        WSGEN("wsgen"),
        WSIMPORT("wsimport"),
        XJC("xjc");
        private String command;
        private Command(String command) { this.command = command; }
        public String get() { return command; }
    }
    
    /* Run one of the Glassfish commands.
     * Each goal calls this method. */
    protected void run(String installPath, Command command, List<String> opts) 
                       throws IOException, MojoFailureException {
        
        /* GlassFish installation directory */
        String instStr = Defaults.glassfishDirectory;
        if (installPath.length() > 0)
            instStr = installPath;
        
        /* GlassFish bin/ directory */
        File commandPath;
        commandPath = new File(new File(instStr), "glassfish");
        commandPath = new File(commandPath, "bin");  
        
        /* Command to execute as a list of strings */
        List<String> cmd = new ArrayList<String>();
        if (System.getProperty("os.name").startsWith("Windows")) {
            commandPath = new File(commandPath, command.get()+".bat");
            cmd.add("cmd.exe");
            cmd.add("/C");
            cmd.add(commandPath.toString());
            cmd.addAll(opts);
        } else {
            commandPath = new File(commandPath, command.get()+".sh");
            cmd.add(commandPath.toString());
            cmd.addAll(opts);
        }
        
        /* Show the full command to execute on the log */
        String cmdDisplay = "";
        for (int i=2; i<cmd.size(); i++)
            cmdDisplay += cmd.get(i) + " ";
        getLog().debug(cmdDisplay);
        
        /* Run the command */
        int exitValue = -1;
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process proc = pb.start();
        
        /* Get streams */
        BufferedReader stdRdr = new BufferedReader(
                                new InputStreamReader(proc.getInputStream()));
        BufferedReader errRdr = new BufferedReader(
                                new InputStreamReader(proc.getErrorStream()));
        
        /* Wait for the command to end and get the result value */
        while (true) {
            try {
                exitValue = proc.exitValue();
                break;
            } catch (IllegalThreadStateException e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    throw new MojoFailureException("Interrupted");
                }
            }
        }
        
        /* Show the output from the command */
        if (exitValue == 0)
            while (stdRdr.ready())
                getLog().info(stdRdr.readLine());
        else
            while (errRdr.ready())
                getLog().info(errRdr.readLine());
        
    }
}
