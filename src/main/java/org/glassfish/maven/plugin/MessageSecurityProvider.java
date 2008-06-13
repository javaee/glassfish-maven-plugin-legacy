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

import au.net.ocean.maven.plugin.annotation.Parameter;
import au.net.ocean.maven.plugin.annotation.Required;

import java.text.MessageFormat;
import java.util.Set;

/**
 * Created by dwhitla at Apr 10, 2007 12:22:48 AM
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: MessageSecurityProvider.java 0 Apr 10, 2007 12:22:48 AM dwhitla $
 */
public class MessageSecurityProvider {

    public enum Layer {
        HttpServlet,
        SOAP
    }

    public enum Type {
        server
    }

    public enum AuthSource {
        sender
    }

    public enum RecipientAuthPhase {
        BeforeContent("before-content");

        private String label;

        private RecipientAuthPhase(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }

        public static RecipientAuthPhase forLabel(String label) throws IllegalArgumentException {
            for (RecipientAuthPhase phase : values()) {
                if (phase.label.equals(label)) {
                    return phase;
                }
            }
            throw new IllegalArgumentException(MessageFormat.format("No RecipientAuthPhase with label \"{0}\"", label));
        }
    }


    @Required
    @Parameter
    private String name;

    @Required
    @Parameter
    private Layer layer;

    @Required
    @Parameter
    private Type type;

    @Required
    @Parameter
    private String className;

    @Parameter
    private AuthSource requestAuthSource;

    @Parameter
    private RecipientAuthPhase requestAuthRecipient;

    @Parameter
    private AuthSource responseAuthSource;

    @Parameter
    private RecipientAuthPhase responseAuthRecipient;

    @Parameter(defaultValue = "localhost")
    private String host;

    @Parameter(property = "default", defaultValue = "false")
    private boolean defaultProvider;

    @Parameter
    private Set<Property> properties;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLayer() {
        return layer.toString();
    }

    public void setLayer(String layer) {
        this.layer = Layer.valueOf(layer);
    }

    public String getType() {
        return type.toString();
    }

    public void setType(String type) {
        this.type = Type.valueOf(type);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRequestAuthSource() {
        return requestAuthSource == null ? null : requestAuthSource.toString();
    }

    public void setRequestAuthSource(String requestAuthSource) {
        this.requestAuthSource = AuthSource.valueOf(requestAuthSource);
    }

    public String getRequestAuthRecipient() {
        return requestAuthRecipient == null ? null : requestAuthRecipient.toString();
    }

    public void setRequestAuthRecipient(String requestAuthRecipient) {
        this.requestAuthRecipient = RecipientAuthPhase.forLabel(requestAuthRecipient);
    }

    public String getResponseAuthSource() {
        return responseAuthSource == null ? null : responseAuthSource.toString();
    }

    public void setResponseAuthSource(String responseAuthSource) {
        this.responseAuthSource = AuthSource.valueOf(responseAuthSource);
    }

    public String getResponseAuthRecipient() {
        return responseAuthRecipient == null ? null : responseAuthRecipient.toString();
    }

    public void setResponseAuthRecipient(String responseAuthRecipient) {
        this.responseAuthRecipient = RecipientAuthPhase.forLabel(responseAuthRecipient);
    }

    public void setDefault(boolean isDefault) {
        this.defaultProvider = isDefault;
    }

    public boolean isDefault() {
        return defaultProvider;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(Set<Property> properties) {
        this.properties = properties;
    }
    
}
