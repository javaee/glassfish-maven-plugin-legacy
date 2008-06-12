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
