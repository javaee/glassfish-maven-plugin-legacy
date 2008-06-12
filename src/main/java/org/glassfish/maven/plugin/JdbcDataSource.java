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

import java.util.Set;

/**
 * Created by dwhitla at Apr 10, 2007 1:23:47 PM
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 */
public class JdbcDataSource implements Resource {

    public enum ValidationMethod {
        autoCommit("auto-commit"),
        metaData("meta-data"),
        table("table");

        private String realName;

        ValidationMethod(String realName) {
            this.realName = realName;
        }

        public String toString() {
            return realName;
        }

        public static ValidationMethod forName(String realName) {
            for (ValidationMethod method : values()) {
                if (method.realName.equalsIgnoreCase(realName)) {
                    return method;
                }
            }
            throw new IllegalArgumentException("No ValidationMethod exists with name \"" + realName + "\"");
        }
    }

    @Required
    @Parameter
    private String poolName;

    @Required
    @Parameter
    private String className;

    @Required
    @Parameter
    private String name;

    @Required
    @Parameter
    private String description;

    @Required
    @Parameter(property = "type")
    private Type _type;

    @Parameter(defaultValue = "true")
    private boolean allowNonComponentCallers = true;

    @Parameter(defaultValue = "false")
    private boolean failConnection;

    @Parameter(defaultValue = "false")
    private boolean validateConnections;

    @Parameter(defaultValue = "false")
    private boolean isolationGuaranteed = false;

    @Parameter(defaultValue = "false")
    private boolean nonTransactionalConnections;

    @Parameter(property = "validationMethod", defaultValue = "auto-commit")
    private ValidationMethod validationMethod = ValidationMethod.autoCommit;

    @Parameter(defaultValue = "2")
    private int poolResize = 2;

    @Parameter(defaultValue = "50")
    private int maxPoolSize = 50;

    @Parameter(defaultValue = "5")
    private int steadyPoolSize = 5;

    @Parameter(defaultValue = "300")
    private int idleTimeout = 300;

    @Parameter(defaultValue = "60000")
    private int maxWait = 60000;

    @Parameter
    private Set<Property> properties;


    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAllowNonComponentCallers() {
        return allowNonComponentCallers;
    }

    public void setAllowNonComponentCallers(boolean allowNonComponentCallers) {
        this.allowNonComponentCallers = allowNonComponentCallers;
    }

    public boolean isFailConnection() {
        return failConnection;
    }

    public void setFailConnection(boolean failConnection) {
        this.failConnection = failConnection;
    }

    public boolean isValidateConnections() {
        return validateConnections;
    }

    public void setValidateConnections(boolean validateConnections) {
        this.validateConnections = validateConnections;
    }

    public boolean isIsolationGuaranteed() {
        return isolationGuaranteed;
    }

    public void setIsolationGuaranteed(boolean isolationGuaranteed) {
        this.isolationGuaranteed = isolationGuaranteed;
    }

    public boolean isNonTransactionalConnections() {
        return nonTransactionalConnections;
    }

    public void setNonTransactionalConnections(boolean nonTransactionalConnections) {
        this.nonTransactionalConnections = nonTransactionalConnections;
    }

    public String getValidationMethod() {
        return validationMethod.toString();
    }

    public void setValidationMethod(String validationMethod) {
        this.validationMethod = ValidationMethod.valueOf(validationMethod);
    }

    public int getPoolResize() {
        return poolResize;
    }

    public void setPoolResize(int poolResize) {
        this.poolResize = poolResize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getSteadyPoolSize() {
        return steadyPoolSize;
    }

    public void setSteadyPoolSize(int steadyPoolSize) {
        this.steadyPoolSize = steadyPoolSize;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public void setProperties(Set<Property> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return _type;
    }

    public void setType(String type) {
        this._type = Type.valueOf(type);
    }
}
