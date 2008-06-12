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

import org.glassfish.maven.plugin.Domain;
import org.glassfish.maven.plugin.GlassfishMojo;

import java.util.List;

/**
 * Created by dwhitla at Apr 9, 2007 4:09:05 PM
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: CreateDomainCommand.java 0 Apr 9, 2007 4:09:05 PM dwhitla $
 */
public class CreateDomainCommand extends InteractiveAsadminCommand {

    private Domain domain;

    public CreateDomainCommand(GlassfishMojo sharedContext, Domain domain) {
        super(sharedContext);
        this.domain = domain;
    }

    protected String getName() {
        return "create-domain";
    }

    protected List<String> getParameters() {
        StringBuilder domainProperties = new StringBuilder();
        if (domain.getHTTPSPort() > 0)
            domainProperties.append("http.ssl.port=").append(domain.getHTTPSPort());
        if (domain.getIIOPPort() > 0) {
            if (domainProperties.length() > 0) domainProperties.append(":");
            domainProperties.append("orb.listener.port=").append(domain.getIIOPPort());
        }
        if (domain.getJMSPort() > 0) {
            if (domainProperties.length() > 0) domainProperties.append(":");
            domainProperties.append("jms.port=").append(domain.getJMSPort());
        }

        List<String> parameters = super.getParameters();
        addOptionalParameter(parameters, "--domaindir", domain.getDirectory());
        addOptionalParameter(parameters, "--profile", domain.getProfile());
        addOptionalParameter(parameters, "--adminport", domain.getAdminPort());
        addOptionalParameter(parameters, "--instanceport", domain.getHTTPPort());
        addOptionalParameter(parameters, "--domainproperties", domainProperties.toString());
        parameters.add(domain.getName());
        return parameters;
    }

    protected String getErrorMessage() {
        return "Unable to create domain \"" + domain.getName() + "\".";
    }
}
