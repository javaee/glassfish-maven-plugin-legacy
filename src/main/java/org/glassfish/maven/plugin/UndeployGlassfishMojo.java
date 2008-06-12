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

import org.glassfish.maven.plugin.command.StartDomainCommand;
import org.glassfish.maven.plugin.command.StopDomainCommand;
import org.glassfish.maven.plugin.command.UndeployCommand;
import au.net.ocean.maven.plugin.annotation.Mojo;
import static au.net.ocean.maven.plugin.annotation.Phase.PostIntegrationTest;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Undeploy JavaEE components which are currently deployed to a domain in a local or remote Glassfish instance
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: UndeployGlassfishMojo.java 0 Apr 2, 2007 7:26:05 PM dwhitla $
 */
@Mojo(
        goal = "undeploy",
        description = "Undeploy JavaEE components which are currently deployed to a domain in a local or remote Glassfish instance",
        phase = PostIntegrationTest
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
            for (Component component : components) {
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
