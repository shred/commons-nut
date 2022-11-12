/*
 * Shredzone Commons - nut
 *
 * Copyright (C) 2022 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.shredzone.commons.nut;

import java.io.IOException;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.shredzone.commons.nut.util.NutSocket;
import org.shredzone.commons.nut.util.Request;

/**
 * Representation of a single variable. This class does not distinguish whether the
 * variable is read only or read/write.
 */
public class Variable {
    private final NutSocket socket;
    private final Device device;
    private final String name;

    @Nullable
    private String value;

    @Nullable
    private String description;

    /**
     * Creates a new {@link Variable} instance.
     *
     * @param name
     *         Variable name.
     * @param device
     *         Device to which the variable is attached.
     */
    Variable(String name, @Nullable String value, Device device, NutSocket socket) {
        this.name = name;
        this.value = value;
        this.device = device;
        this.socket = socket;
    }

    /**
     * Returns the variable name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the variable description. The result is cached.
     *
     * @throws IOException
     *         if an error occured while fetching the description from the server.
     */
    public String getDescription() throws IOException {
        if (description == null) {
            var res = socket.query(Request.get("DESC").device(device).arg(name));
            description = res.get(2);
        }
        return description;
    }

    /**
     * Returns the value of that variable. The result is cached.
     *
     * @throws IOException
     *         if an error occured while fetching the value from the server.
     */
    public String getValue() throws IOException {
        if (value == null) {
            var res = socket.query(Request.get("VAR").device(device).arg(name));
            value = res.get(3);
        }
        return value;
    }

    /**
     * Changes the value of a read/write variable. The new value is also cached.
     */
    public void setValue(String value) throws IOException {
        socket.execute(Request.set("VAR").device(device).arg(name).arg(value));
        this.value = value;
    }

    /**
     * Purges all locally cached values.
     */
    public void purge() {
        value = null;
        description = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Variable: ").append(name);
        if (value != null) {
            sb.append(" = \"").append(value).append('"');
        }
        if (description != null) {
            sb.append(" (").append(description).append(')');
        }
        return sb.toString();
    }

}
