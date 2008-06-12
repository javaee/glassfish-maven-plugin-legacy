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

import org.glassfish.maven.plugin.command.AddResourcesCommand;
import org.glassfish.maven.plugin.command.CreateAuthRealmCommand;
import org.glassfish.maven.plugin.command.CreateDomainCommand;
import org.glassfish.maven.plugin.command.CreateJDBCConnectionPoolCommand;
import org.glassfish.maven.plugin.command.CreateJDBCResourceCommand;
import org.glassfish.maven.plugin.command.CreateJMSDestinationCommand;
import org.glassfish.maven.plugin.command.CreateJMSResourceCommand;
import org.glassfish.maven.plugin.command.CreateJVMOptionsCommand;
import org.glassfish.maven.plugin.command.CreateMessageSecurityProviderCommand;
import org.glassfish.maven.plugin.command.SetCommand;
import org.glassfish.maven.plugin.command.StartDomainCommand;
import org.glassfish.maven.plugin.command.StopDomainCommand;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.util.Set;

/**
 * Created by Dave Whitla on 23/03/2008 at 21:54:07
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: CreateDomainMacro.java 0 23/03/2008 21:54:07 dwhitla $
 */
public class CreateDomainMacro {
    
    private GlassfishMojo sharedContext;
    private Domain domain;

    public CreateDomainMacro(GlassfishMojo sharedContext, Domain domain) {
        this.sharedContext = sharedContext;
        this.domain = domain;
    }

    public void execute(ProcessBuilder processBuilder) throws MojoExecutionException, MojoFailureException {
        new CreateDomainCommand(sharedContext, domain).execute(processBuilder);
        new StartDomainCommand(sharedContext, domain).execute(processBuilder);
        createJVMOptions(processBuilder);
        addResources(processBuilder);
        setProperties(processBuilder);
        createAuth(processBuilder);
        new StopDomainCommand(sharedContext, domain).execute(processBuilder);
    }

    private void createJVMOptions(ProcessBuilder processBuilder) throws MojoExecutionException {
        if (domain.getJVMOptions() != null) {
            new CreateJVMOptionsCommand(sharedContext, domain).execute(processBuilder);
        }
    }

    private void setProperties(ProcessBuilder processBuilder) throws MojoExecutionException {
        if (domain.getProperties() != null) {
            for (Property property : domain.getProperties()) {
                new SetCommand(sharedContext, domain, property).execute(processBuilder);
            }
        }
    }

    private void addResources(ProcessBuilder processBuilder) throws MojoExecutionException {
        if (domain.getResourceDescriptor() != null) {
            new AddResourcesCommand(sharedContext, domain).execute(processBuilder);
        }
        Set<Resource> resources = domain.getResources();
        if (resources != null) {
            for (Resource resource : resources) {
                if (resource instanceof JmsDestination) {
                    createJMSDestination(processBuilder, (JmsDestination) resource);
                } else if (resource instanceof ConnectionFactory) {
                    new CreateJMSResourceCommand(sharedContext, domain, (ConnectionFactory) resource).execute(processBuilder);
                } else if (resource instanceof JdbcDataSource) {
                    createDataSource(processBuilder, (JdbcDataSource) resource);
                }
            }
        }
    }

    private void createDataSource(ProcessBuilder processBuilder, JdbcDataSource jdbcDataSource)
            throws MojoExecutionException {
        new CreateJDBCConnectionPoolCommand(sharedContext, domain, jdbcDataSource).execute(processBuilder);
        new CreateJDBCResourceCommand(sharedContext, domain, jdbcDataSource).execute(processBuilder);
    }

    private void createJMSDestination(ProcessBuilder processBuilder, JmsDestination jmsDestination) throws MojoExecutionException {
        new CreateJMSResourceCommand(sharedContext, domain, jmsDestination.getConnectionFactory()).execute(processBuilder);
        new CreateJMSDestinationCommand(sharedContext, domain, jmsDestination.getDestination()).execute(processBuilder);
        new CreateJMSResourceCommand(sharedContext, domain, jmsDestination).execute(processBuilder);
    }

    private void createAuth(ProcessBuilder processBuilder) throws MojoExecutionException {
        Auth auth = domain.getAuth();
        if (auth != null) {
            createAuthRealm(processBuilder, auth);
            setMessageSecurityProvider(processBuilder, auth);
        }
    }

    private void createAuthRealm(ProcessBuilder processBuilder, Auth auth) throws MojoExecutionException {
        Realm realm = auth.getRealm();
        if (realm != null) {
            new CreateAuthRealmCommand(sharedContext, domain, realm).execute(processBuilder);
            Property property = new Property("server.security-service.default-realm", realm.getName());
            new SetCommand(sharedContext, domain, property).execute(processBuilder);
        }
    }

    private void setMessageSecurityProvider(ProcessBuilder processBuilder, Auth auth) throws MojoExecutionException {
        MessageSecurityProvider securityProvider = auth.getMessageSecurityProvider();
        if (securityProvider != null) {
            new CreateMessageSecurityProviderCommand(sharedContext, domain, securityProvider).execute(processBuilder);
        }
    }
}
