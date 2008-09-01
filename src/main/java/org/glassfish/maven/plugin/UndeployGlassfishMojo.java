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

import au.net.ocean.maven.plugin.annotation.Mojo;
import static au.net.ocean.maven.plugin.annotation.Phase.PostIntegrationTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.glassfish.maven.plugin.command.StartDomainCommand;
import org.glassfish.maven.plugin.command.StopDomainCommand;
import org.glassfish.maven.plugin.command.UndeployCommand;

/**
 * Undeploy JavaEE components which are currently deployed to a domain in a local or remote Glassfish instance
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: UndeployGlassfishMojo.java 0 Apr 2, 2007 7:26:05 PM dwhitla $
 */
@Mojo(
        goal = "undeploy",
        description =
                "Undeploy JavaEE components which are currently deployed to a domain in a local or remote Glassfish instance",
        phase = PostIntegrationTest,
        requiresProject = true
)
public class UndeployGlassfishMojo extends DeploymentGlassfishMojo {

    public void doExecute() throws MojoExecutionException, MojoFailureException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (domain.exists()) {
            boolean started = domain.isStarted();
            if (!started) {
                getLog().info("Domain " + domain.getName() + " isn't started. Starting it for you.");
                new StartDomainCommand(this, domain).execute(processBuilder);
            }
            for (Component component : getComponents()) {
                new UndeployCommand(this, domain, component).execute(processBuilder);
            }
            if (!started) {
                new StopDomainCommand(this, domain).execute(processBuilder);
            }
        } else {
            throw new MojoFailureException("Domain " + domain.getName() + " does not exist.");
        }
    }

}
