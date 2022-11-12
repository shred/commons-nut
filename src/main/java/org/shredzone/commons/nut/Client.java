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
import org.shredzone.commons.nut.exception.NutException;
import org.shredzone.commons.nut.util.NutSocket;
import org.shredzone.commons.nut.util.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A client that connects to a NUT server.
 * <p>
 * This is the starting point for connecting to your server.
 * <p>
 * Note that the client (and all the related classes) are not threadsafe.
 */
public class Client implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3493;

    private final NutSocket socket;
    private final String server;
    private final String protocol;

    /**
     * Creates a client that connects to the default port 3493 at localhost, and does not
     * use authentication.
     */
    public Client() throws IOException {
        this(DEFAULT_HOST);
    }

    /**
     * Creates a client that connects to the default port 3493 at the given host, and does
     * not use authentication.
     *
     * @param host
     *         Host name to connect to
     */
    public Client(String host) throws IOException {
        this(host, DEFAULT_PORT);
    }

    /**
     * Creates a client that connects to the given host and port, and does not use
     * authentication.
     *
     * @param host
     *         Host name to connect to
     * @param port
     *         TCP port to connect to
     */
    public Client(String host, int port) throws IOException {
        this(host, port, null, null);
    }

    /**
     * Creates a client that connects to the default port 3493 at the given host, and logs
     * in with the given credentials.
     *
     * @param host
     *         Host name to connect to
     * @param login
     *         Login name, or {@code null} to skip authentication
     * @param password
     *         Password, or {@code null} to skip authentication
     */
    public Client(String host, @Nullable String login, @Nullable String password)
            throws IOException {
        this(host, DEFAULT_PORT, login, password);
    }

    /**
     * Creates a client that connects to the given host and port, and logs in with the
     * given credentials.
     *
     * @param host
     *         Host name to connect to
     * @param port
     *         TCP port to connect to
     * @param login
     *         Login name, or {@code null} to skip authentication
     * @param password
     *         Password, or {@code null} to skip authentication
     */
    public Client(String host, int port, @Nullable String login, @Nullable String password)
            throws IOException {
        socket = new NutSocket(host, port);
        if (login != null && password != null) {
            socket.execute(Request.username().arg(login));
            socket.execute(Request.password().arg(password));
        }
        server = socket.query(new Request("VER")).getRaw();
        protocol = socket.query(new Request("NETVER")).getRaw();
        LOG.info("Connected to {}:{}, protocol {}, {}", host, port, protocol, server);
    }

    /**
     * Checks if the client is still connected. Note that this method does not detect if
     * the connection was closed by the server.
     */
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Closes the client, and forces a disconnect.
     * <p>
     * Create a new client for reconnection.
     */
    @Override
    public void close() throws IOException {
        if (isConnected()) {
            socket.close();
            LOG.info("Disconnected");
        }
    }

    /**
     * Logs out from the server, and then closes the connection.
     * <p>
     * Create a new client for reconnection.
     */
    public void logout() throws IOException, NutException {
        if (isConnected()) {
            socket.execute(Request.logout());
            LOG.info("Logged out");
            close();
        }
    }

    /**
     * Retrieve a list of available devices.
     *
     * @return List of UPS {@link Device} available on the server
     */
    public List<Device> getDeviceList() throws IOException, NutException {
        return socket.list(Request.list("UPS")).stream()
                .map(req -> new Device(req.get(1), req.get(2), socket))
                .collect(toList());
    }

    /**
     * Returns the {@link Device} with the given name.
     * <p>
     * Note that the instance is generated irregarding of the existence of such a device.
     *
     * @param name
     *         UPS device name
     * @return Device instance
     */
    public Device getDevice(String name) {
        return new Device(name, null, socket);
    }

    /**
     * Returns the server version.
     */
    public String getServer() {
        return server;
    }

    /**
     * Returns the NUT protocol version.
     */
    public String getProtocol() {
        return protocol;
    }

}
