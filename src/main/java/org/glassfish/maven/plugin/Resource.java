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

import java.util.Set;

/**
 * Created by dwhitla at Apr 10, 2007 10:14:48 AM
 *
 * @author <a href="mailto:dave.whitla@ocean.net.au">Dave Whitla</a>
 * @version $Id: Resource.java 0 Apr 10, 2007 10:14:48 AM dwhitla $
 */
public interface Resource {

    public enum Type {
        topic("javax.jms.Topic"),
        queue("javax.jms.Queue"),
        connectionFactory("javax.jms.ConnectionFactory"),
        topicConnectionFactory("javax.jms.TopicConnectionFactory"),
        queueConnectionFactory("javax.jms.QueueConnectionFactory"),
        dataSource("javax.sql.DataSource"),
        connectionPoolDataSource("javax.sql.ConnectionPoolDataSource");

        private String qName;

        Type(String qName) {
            this.qName = qName;
        }

        public String toString() {
            return qName;
        }

        public static Type forName(String name) {
            for (Type type : values()) {
                if (type.qName.equalsIgnoreCase(name)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No resource type exists with qualified name \"" + name + "\"");
        }
    }

    Type getType();

    void setProperties(Set<Property> properties);
    Set<Property> getProperties();
}
