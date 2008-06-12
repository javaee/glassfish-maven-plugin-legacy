/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */

package org.glassfish.maven.plugin;

import au.net.ocean.maven.plugin.annotation.Mojo;
import org.glassfish.maven.plugin.command.DeleteDomainCommand;
import org.glassfish.maven.plugin.command.StopDomainCommand;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * Create a new virtual server in a local or remote Glassfish instance
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: CreateVirtualServerMojo.java 0 26/03/2008 14:44:57 dwhitla $
 */
@Mojo(
        goal = "create-virtual-server",
        description = "Create a new virtual server in a local or remote Glassfish instance"
)
public class CreateVirtualServerMojo extends GlassfishMojo {

    public void doExecute() throws MojoExecutionException, MojoFailureException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        Log log = getLog();
        if (domain.exists()) {
            log.info("Domain " + domain.getName() + " exists.");
            if (domain.isReuse()) {
                log.info("Reusing it.");
                return;
            } else {
                log.info("Deleting it.");
                if (domain.isStarted()) {
                    new StopDomainCommand(this, domain).execute(processBuilder);
                }
                new DeleteDomainCommand(this, domain).execute(processBuilder);
            }
        }
        new CreateDomainMacro(this, domain).execute(processBuilder);
    }

}
