/*
 * (c) Copyright 2013 - The gf-maven-plugin developers. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or license/LICENSE.html.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at license/LICENSE.html.
 * This file is subject to the "Classpath" exception as provided by the 
 * copyright holder in the GPL Version 2 section of the
 * License file that accompanied this code.  If applicable, add the following
 * below the License Header, with the fields enclosed by brackets [] replaced
 * by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */
package org.glassfish.maven.plugin;

import java.util.HashMap;
import java.util.Map;

/* Default values for parameters not specified on a project's POM */
public class Defaults {
    
    /* The POM-equivalent defaults are:
     * <build>
        <plugins>
            <plugin>
                <groupId>org.glassfish.maven.plugin</groupId>
                <artifactId>maven-glassfish-plugin</artifactId>
                <version>3.0.2</version>
                <configuration>
                    <glassfishDirectory>C:\\glassfish4</glassfishDirectory>
                    <domainDirectory>C:\\glassfish4\\domains</domainDirectory>
                    <domain>
                        <name>domain1</name>
                    </domain>
                </configuration>
            </plugin>
         </plugins>
        </build> */

    public static final  String glassfishDirectory = "C:\\glassfish4\\";
    public static final String domainDirectory = glassfishDirectory 
                                                 + "glassfish\\domains";
    public static final Map<String,Object> domain = new HashMap<String,Object>();
    static {
        domain.put("name", "domain1");
    }
    
}