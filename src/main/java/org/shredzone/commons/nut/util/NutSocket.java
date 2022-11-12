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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.shredzone.commons.nut.exception.InvalidResponseException;
import org.shredzone.commons.nut.exception.NutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A socket that is used for communication with a NUT server.
 */
public class NutSocket implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(NutSocket.class);

    private final Socket socket;
    private final OutputStreamWriter writer;
    private final BufferedReader reader;

    /**
     * Creates a new {@link NutSocket}. It immediately connects to the server.
     *
     * @param host
     *         Server host name
     * @param port
     *         Server port
     */
    public NutSocket(String host, int port) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
        writer = new OutputStreamWriter(socket.getOutputStream(), UTF_8);
    }

    /**
     * Sends a request to the server, and expects to get a simple "OK" as response.
     *
     * @param request
     *         {@link Request} to send
     */
    public void execute(Request request) throws IOException {
        send(request);
        var response = receive();
        if (!response.get(0).equals("OK")) {
            throw new InvalidResponseException("Expected OK or ERR", response.getRaw());
        }
    }

    /**
     * Sends a query to the server, and expects a single-line response.
     *
     * @param request
     *         {@link Request} to send
     * @return The {@link Response} that was returned by the server.
     */
    public Response query(Request request) throws IOException {
        send(request);
        var query = request.getRequest();
        var queryWithoutCommand = query.subList(1, query.size());
        var response = receive();
        if (!matches(response.getAll(), queryWithoutCommand)) {
            throw new InvalidResponseException("Unexpected answer", response.getRaw());
        }
        return response;
    }

    /**
     * Sends a query to the server, and expects a list response.
     *
     * @param request
     *         {@link Request} to send
     * @return A list of single {@link Response} that was returned by the server.
     */
    public List<Response> list(Request request) throws IOException {
        send(request);

        var query = request.getRequest();
        var queryWithoutCommand = query.subList(1, query.size());

        var line = receive();
        if (!matches(line.getAll(), query, "BEGIN")) {
            throw new InvalidResponseException("BEGIN is missing", line.getRaw());
        }

        var result = new ArrayList<Response>();
        line = receive();
        while(!matches(line.getAll(), query, "END")) {
            if (!matches(line.getAll(), queryWithoutCommand)) {
                throw new InvalidResponseException("Unexpected record type", line.toString());
            }
            result.add(line);
            line = receive();
        }
        return result;
    }

    /**
     * Checks if the socket is still connected.
     *
     * @return {@code true} if the socket is still connected.  Note that this method does
     * not detect if the socket was closed by the server.
     */
    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }

    /**
     * Closes the socket. It cannot be used after that.
     */
    @Override
    public void close() throws IOException {
        try {
            writer.close();
        } catch (IOException ex) {
            // Can be ignored in favor of closing the socket itself.
            LOG.debug("Exception while closing output stream", ex);
        }

        try {
            reader.close();
        } catch (IOException ex) {
            // Can be ignored in favor of closing the socket itself.
            LOG.debug("Exception while closing input stream", ex);
        }

        socket.close();
    }

    /**
     * Sends a request to the server.
     *
     * @param request
     *         {@link Request} to send
     */
    private void send(Request request) throws IOException {
        var req = request.toString();
        LOG.debug(" -> " + req);
        writer.append(req).append('\n').flush();
    }

    /**
     * Receives a single response line from the service.
     *
     * @return The {@link Response}, parsed and unqouted
     */
    private Response receive() throws IOException {
        var line = reader.readLine();
        if (line == null) {
            throw new EOFException("Stream was unexpectedly closed");
        }

        LOG.debug(" <- {}", line);

        var response = new Response(line);
        if (response.get(0).equals("ERR")) {
            throw new NutException(response.get(1));
        }

        return response;
    }

    /**
     * Checks if a response meets the expectations.
     *
     * @param response
     *         Response to check
     * @param match
     *         Parts that are expected to be found in the response
     * @param prefix
     *         Fixed prefixes that are expected in the response prior to the match
     * @return {@code true} if the response matches
     */
    private boolean matches(List<String> response, List<String> match, String... prefix) {
        if (response.size() < prefix.length) {
            return false;
        }

        if (!response.subList(0, prefix.length).equals(Arrays.asList(prefix))) {
            return false;
        }

        if (response.size() < prefix.length + match.size()) {
            return false;
        }

        return (response.subList(prefix.length, prefix.length + match.size())).equals(match);
    }

}
