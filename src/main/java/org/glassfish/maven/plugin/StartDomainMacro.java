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
