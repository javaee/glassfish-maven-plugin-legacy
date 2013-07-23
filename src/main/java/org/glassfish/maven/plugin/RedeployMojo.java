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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Redeploys the project to GlassFish
 * 
 * @goal redeploy
 * @requiresProject true
 */
public class RedeployMojo extends GlassFishMojo {

    /**
     * @parameter
     */
    protected String glassfishDirectory;   
    /**
     * @parameter expression="${project.build.finalName}"
     */
    protected String application;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        
        try {
            /* Gather options */
            if (glassfishDirectory == null)
                glassfishDirectory = Defaults.glassfishDirectory;
            
            File appPath = new File("target");
            appPath = new File(appPath, application);
            List<String> options = new ArrayList<String>();
            options.add("redeploy");
            options.add("--name");
            options.add(application);
            options.add(appPath.toString());
            
            /* Redeploy the app */
            run(glassfishDirectory, GlassFishMojo.Command.ASADMIN, options);
        } catch (IOException ex) {
            throw new MojoExecutionException(ex.toString());
        }
    }
    
}
