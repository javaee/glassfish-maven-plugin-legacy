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

import org.glassfish.maven.plugin.command.DeployCommand;
import au.net.ocean.maven.plugin.annotation.Mojo;
import au.net.ocean.maven.plugin.annotation.Phase;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Deploy JavaEE component artifacts to domain in a local or remote Glassfish instance
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: DeployGlassfishMojo.java 0 Apr 2, 2007 7:16:12 PM dwhitla $
 */
@Mojo(
        goal = "deploy",
        phase = Phase.PreIntegrationTest,
        description = "Deploy JavaEE component artifacts to domain in a local or remote Glassfish instance"
)
public class DeployGlassfishMojo extends DeploymentGlassfishMojo {

    public void doExecute() throws MojoExecutionException, MojoFailureException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (!domain.isStarted()) {
            getLog().info("Domain " + domain.getName() + " isn't started. Starting it for you.");
            new StartDomainMacro(this, domain).execute(processBuilder);
        }
        for (Component component : components) {
            new DeployCommand(this, domain, component).execute(processBuilder);
        }
    }

}
