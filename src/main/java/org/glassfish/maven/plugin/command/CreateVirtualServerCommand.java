/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */

package org.glassfish.maven.plugin.command;

import org.glassfish.maven.plugin.GlassfishMojo;

/**
 * Created by Dave Whitla on 26/03/2008 at 14:48:13
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: CreateVirtualServerCommand.java 0 26/03/2008 14:48:13 dwhitla $
 */
public class CreateVirtualServerCommand extends InteractiveAsadminCommand {

    String command = "create-virtual-server --hosts hosts [--terse=false] [--echo=false] [--interactive=true] [--host localhost] [--port 4848|4849] [--secure | -s] [--user admin_user] [--passwordfile file_name] [--httplisteners httplisteners] [--defaultwebmodule default_web_module] [--state on] [--logfile logfile] [--property (name=value)[:name=value]*] [--target target(Default server)] virtual_server_id";

    protected CreateVirtualServerCommand(GlassfishMojo sharedContext) {
        super(sharedContext);
    }

    protected String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected String getErrorMessage() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
