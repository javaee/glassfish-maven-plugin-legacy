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

import org.glassfish.maven.plugin.DeploymentGlassfishMojo;
import org.glassfish.maven.plugin.Domain;
import org.glassfish.maven.plugin.Component;

import java.util.List;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by dwhitla at Apr 9, 2007 4:36:52 PM
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: DeployCommand.java 0 Apr 9, 2007 4:36:52 PM dwhitla $
 */
public class DeployCommand extends InteractiveAsadminCommand {

    private Domain domain;
    private Component component;

    public DeployCommand(DeploymentGlassfishMojo sharedContext, Domain domain, Component component) {
        super(sharedContext);
        this.domain = domain;
        this.component = component;
    }

    protected String getName() {
        return "deploy";
    }

    protected List<String> getParameters() {
        boolean upload = true;
        try {
            upload = !InetAddress.getByName(domain.getHost()).isLoopbackAddress();
        } catch (UnknownHostException e) {
            // ignore
        }
        List<String> parameters = super.getParameters();
        parameters.addAll(Arrays.asList(
                "--host", domain.getHost(),
                "--port", String.valueOf(domain.getAdminPort()),
                "--upload=" + upload,
                "--name", component.getName(),
                component.getArtifact().getAbsolutePath()
        ));
        return parameters;
    }

    protected String getErrorMessage() {
        return "Deployment of " + component.getArtifact().getAbsolutePath() + " failed.";
    }
}
