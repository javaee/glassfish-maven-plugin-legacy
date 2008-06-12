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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * Created by Dave Whitla on 24/03/2008 at 00:43:11
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: StartDomainMacro.java 0 24/03/2008 00:43:11 dwhitla $
 */
public class StartDomainMacro {
    
    private GlassfishMojo sharedContext;
    private Domain domain;

    public StartDomainMacro(GlassfishMojo sharedContext, Domain domain) {
        this.sharedContext = sharedContext;
        this.domain = domain;
    }

    public void execute(ProcessBuilder processBuilder) throws MojoFailureException, MojoExecutionException {
        if (!domain.exists()) {
            Log log = sharedContext.getLog();
            if (sharedContext.isAutoCreate()) {
                log.info("Domain " + domain.getName() + " does not exist. Creating it for you.");
                new CreateDomainMacro(sharedContext, domain).execute(processBuilder);
            } else {
                throw new MojoFailureException("Domain " + domain.getName()
                        + " does not exist and \"autoCreate\" is \"false\". Try 'mvn glassfish:createDomain' first.");
            }
        }
        new StartDomainCommand(sharedContext, domain).execute(processBuilder);
    }
}
