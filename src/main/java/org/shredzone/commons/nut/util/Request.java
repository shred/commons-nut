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
package org.shredzone.commons.nut.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.shredzone.commons.nut.Device;

/**
 * Builder for a request that is sent to the service.
 */
public class Request {

    private final String command;
    private final List<String> arguments = new ArrayList<>();

    @Nullable
    private String subcommand;

    /**
     * A {@code GET} request.
     */
    public static Request get(String subcommand) {
        return new Request("GET").sub(subcommand);
    }

    /**
     * A {@code LIST} request.
     */
    public static Request list(String subcommand) {
        return new Request("LIST").sub(subcommand);
    }

    /**
     * A {@code SET} request.
     */
    public static Request set(String subcommand) {
        return new Request("SET").sub(subcommand);
    }

    /**
     * An {@code INSTCMD} request.
     */
    public static Request instcmd() {
        return new Request("INSTCMD");
    }

    /**
     * A {@code LOGOUT} request.
     */
    public static Request logout() {
        return new Request("LOGOUT");
    }

    /**
     * A {@code LOGIN} request.
     */
    public static Request login() {
        return new Request("LOGIN");
    }

    /**
     * A {@code PRIMARY} request.
     */
    public static Request primary() {
        return new Request("PRIMARY");
    }

    /**
     * A {@code FSD} request.
     */
    public static Request fsd() {
        return new Request("FSD");
    }

    /**
     * A {@code PASSWORD} request.
     */
    public static Request password() {
        return new Request("PASSWORD");
    }

    /**
     * A {@code USERNAME} request.
     */
    public static Request username() {
        return new Request("USERNAME");
    }

    /**
     * A {@code STARTTLS} request.
     * <p>
     * Note that STARTTLS is not currently supported by this library.
     */
    public static Request starttls() {
        return new Request("STARTTLS");
    }

    /**
     * Creates a new request with the given command.
     *
     * @param command Request command
     */
    public Request(String command) {
        this.command = command;
    }

    /**
     * Sets an optional subcommand.
     *
     * @param subcommand Request subcommand
     */
    public Request sub(String subcommand) {
        this.subcommand = subcommand;
        return this;
    }

    /**
     * Sets the {@link Device} to be addressed by the request.
     * <p>
     * Note that for this parameter, the invocation order is important.
     *
     * @param device Device
     */
    public Request device(Device device) {
        return arg(device.getName());
    }

    /**
     * Sets an argument. Arguments with spaces or quote characters are quoted and escaped
     * automatically.
     * <p>
     * Note that for this parameter, the invocation order is important.
     *
     * @param arg
     *         Argument
     */
    public Request arg(String arg) {
        arguments.add(arg);
        return this;
    }

    /**
     * Adds a collection of arguments. Arguments with spaces or quote characters are
     * quoted and escaped automatically.
     * <p>
     * Note that for this parameter, the invocation order is important.
     *
     * @param args
     *         Collection of arguments
     */
    public Request args(Collection<String> args) {
        arguments.addAll(args);
        return this;
    }

    /**
     * Returns the request as a list of single parameters.
     */
    public List<String> getRequest() {
        var result = new ArrayList<String>();
        result.add(command);
        if (subcommand != null) {
            result.add(subcommand);
        }
        result.addAll(arguments);
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns the request in a format that can be sent to the server via socket.
     */
    @Override
    public String toString() {
        var result = new StringBuilder(command);

        if (subcommand != null) {
            result.append(' ').append(subcommand);
        }

        arguments.stream()
                .map(StringUtils::quote)
                .forEach(it -> result.append(' ').append(it));

        return result.toString();
    }

}
