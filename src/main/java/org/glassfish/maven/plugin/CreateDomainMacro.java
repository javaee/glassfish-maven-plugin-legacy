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

    private void createJVMOptions(ProcessBuilder processBuilder) throws MojoExecutionException, MojoFailureException {
        if (domain.getJVMOptions() != null) {
            new CreateJVMOptionsCommand(sharedContext, domain).execute(processBuilder);
        }
    }

    private void setProperties(ProcessBuilder processBuilder) throws MojoExecutionException, MojoFailureException {
        if (domain.getProperties() != null) {
            for (Property property : domain.getProperties()) {
                new SetCommand(sharedContext, domain, property).execute(processBuilder);
            }
        }
    }

    private void addResources(ProcessBuilder processBuilder) throws MojoExecutionException, MojoFailureException {
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
            throws MojoExecutionException, MojoFailureException {
        new CreateJDBCConnectionPoolCommand(sharedContext, domain, jdbcDataSource).execute(processBuilder);
        new CreateJDBCResourceCommand(sharedContext, domain, jdbcDataSource).execute(processBuilder);
    }

    private void createJMSDestination(ProcessBuilder processBuilder, JmsDestination jmsDestination)
            throws MojoExecutionException, MojoFailureException {
        new CreateJMSResourceCommand(sharedContext, domain, jmsDestination.getConnectionFactory()).execute(processBuilder);
        new CreateJMSDestinationCommand(sharedContext, domain, jmsDestination.getDestination()).execute(processBuilder);
        new CreateJMSResourceCommand(sharedContext, domain, jmsDestination).execute(processBuilder);
    }

    private void createAuth(ProcessBuilder processBuilder) throws MojoExecutionException, MojoFailureException {
        Auth auth = domain.getAuth();
        if (auth != null) {
            createAuthRealm(processBuilder, auth);
            setMessageSecurityProvider(processBuilder, auth);
        }
    }

    private void createAuthRealm(ProcessBuilder processBuilder, Auth auth) throws MojoExecutionException, MojoFailureException {
        Realm realm = auth.getRealm();
        if (realm != null) {
            new CreateAuthRealmCommand(sharedContext, domain, realm).execute(processBuilder);
            Property property = new Property("server.security-service.default-realm", realm.getName());
            new SetCommand(sharedContext, domain, property).execute(processBuilder);
        }
    }

    private void setMessageSecurityProvider(ProcessBuilder processBuilder, Auth auth)
            throws MojoExecutionException, MojoFailureException {
        MessageSecurityProvider securityProvider = auth.getMessageSecurityProvider();
        if (securityProvider != null) {
            new CreateMessageSecurityProviderCommand(sharedContext, domain, securityProvider).execute(processBuilder);
        }
    }
}
