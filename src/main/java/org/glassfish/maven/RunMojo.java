/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
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
 *
 */

package org.glassfish.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Resource;
import org.glassfish.embed.GFApplication;
import org.glassfish.embed.GlassFish;
import org.glassfish.embed.ScatteredWar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Executes GlassFish v3 inside the current Maven and deploys the application being developed.
 *
 * @goal run
 * @execute phase=compile
 * @requiresDependencyResolution runtime
 * 
 * @author Kohsuke Kawaguchi
 */
public class RunMojo extends AbstractMojo {

    /**
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Directory for static resources, JSPs, etc.
     *
     * @parameter expression="${basedir}/src/main/webapp"
     */
    protected File resourcesDirectory;

    /**
     * If web.xml needs to be picked up from another location,
     * you can do that by specifying its location here.
     *
     * <p>
     * Defaults to {@code $resourceDirectory/WEB-INF/web.xml}
     *
     * @parameter
     */
    protected File webXml;

    public void execute() throws MojoExecutionException, MojoFailureException {
        GlassFish glassfish = new GlassFish();

        List<URL> classpath = new ArrayList<URL>();

        try {
            for( Artifact a : (Set<Artifact>)project.getArtifacts() ) {
                classpath.add(a.getFile().toURI().toURL());
            }
            // resources, so that changes take effect in real time
            for (Resource res : (List<Resource>)project.getBuild().getResources()) {
                classpath.add(new File(res.getDirectory()).toURI().toURL());
            }
            // main artifacts
            classpath.add(new File(project.getBuild().getOutputDirectory()).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Failed to convert to URL",e);
        }

        ScatteredWar war = new ScatteredWar(
            project.getArtifactId(),
            resourcesDirectory,
            webXml,
            classpath
        );

        try {
            while(true) {
                GFApplication app = glassfish.deploy(war);

                System.out.println("Hit ENTER for redeploy");

                // wait for enter
                new BufferedReader(new InputStreamReader(System.in)).readLine();

                app.undeploy();
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(),e);
        }
    }
}
