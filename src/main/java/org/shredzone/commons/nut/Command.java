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
import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.shredzone.commons.nut.util.NutSocket;
import org.shredzone.commons.nut.util.Request;

/**
 * A command that can be executed on the device.
 */
public class Command {
    private final NutSocket socket;
    private final Device device;
    private final String name;

    @Nullable
    private String description;

    /**
     * Creates a new {@link Command} instance.
     *
     * @param name
     *         Command name.
     * @param device
     *         Device to which the command is attached.
     */
    Command(String name, Device device, NutSocket socket) {
        this.name = name;
        this.device = device;
        this.socket = socket;
    }

    /**
     * Returns the command name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the command description.
     */
    public String getDescription() throws IOException {
        if (description == null) {
            var res = socket.query(Request.get("CMDDESC").device(device).arg(name));
            description = res.get(3);
        }
        return description;
    }

    /**
     * Executes the command.
     *
     * @param args
     *         Optional arguments to be passed to the command.
     */
    public void execute(String... args) throws IOException {
        socket.execute(Request.instcmd().device(device).arg(name).args(Arrays.asList(args)));
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
        sb.append("Command: ").append(name);
        if (description != null) {
            sb.append(" (").append(description).append(')');
        }
        return sb.toString();
    }

}
