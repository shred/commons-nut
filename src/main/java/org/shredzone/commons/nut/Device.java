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

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.shredzone.commons.nut.util.NutSocket;
import org.shredzone.commons.nut.util.Request;

/**
 * Representation of a UPS device.
 */
public class Device {
    private final NutSocket socket;
    private final String name;

    @Nullable
    private String description;

    /**
     * Creates a new {@link Device} instance.
     *
     * @param name
     *         Name of the device
     * @param socket
     *         Connection to the NUT server
     */
    Device(String name, @Nullable String description, NutSocket socket) {
        this.name = name;
        this.description = description;
        this.socket = socket;
    }

    /**
     * Returns the device name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the device description. The result is cached.
     *
     * @throws IOException
     *         if an error occured while fetching the description from the server.
     */
    public String getDescription() throws IOException {
        if (description == null) {
            var response = socket.query(Request.get("UPSDESC").device(this));
            description = response.get(2);
        }
        return description;
    }

    /**
     * Returns a list of all variables (read only and read/write).
     */
    public List<Variable> getVariables() throws IOException {
        return socket.list(Request.list("VAR").device(this)).stream()
                .map(res -> new Variable(res.get(2), res.get(3), this, socket))
                .collect(toList());
    }

    /**
     * Returns a list of all read/write variables.
     */
    public List<Variable> getRWVariables() throws IOException {
        return socket.list(Request.list("RW").device(this)).stream()
                .map(res -> new Variable(res.get(2), res.get(3), this, socket))
                .collect(toList());
    }

    /**
     * Returns a variable with the given name.
     * <p>
     * Note that the instance is generated irregarding of the existence of such a
     * variable.
     *
     * @param name
     *         Variable name
     * @return Variable
     */
    public Variable getVariable(String name) {
        return new Variable(name, null, this, socket);
    }

    /**
     * Returns a list of all available commands.
     */
    public List<Command> getCommands() throws IOException {
        return socket.list(Request.list("CMD").device(this)).stream()
                .map(l -> l.get(2))
                .map(n -> new Command(n, this, socket))
                .collect(toList());
    }

    /**
     * Returns a command with the given name.
     * <p>
     * Note that the instance is generated irregarding of the existence of such a
     * command.
     *
     * @param name
     *         Command name
     * @return Command
     */
    public Command getCommand(String name) {
        return new Command(name, this, socket);
    }

    /**
     * Returns the current number of logins on the device. The result is not cached.
     */
    public int getNumberOfLogins() throws IOException {
        return socket.query(Request.get("NUMLOGINS").device(this))
                .getAsNumber(2)
                .intValue();
    }

    /**
     * Logs into this device, incrementing the server side counter of logins.
     */
    public void login() throws IOException {
        socket.execute(Request.login().device(this));
    }

    /**
     * Purges all locally cached values.
     */
    public void purge() {
        description = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Device: ").append(name);
        if (description != null) {
            sb.append(" (").append(description).append(')');
        }
        return sb.toString();
    }

}
