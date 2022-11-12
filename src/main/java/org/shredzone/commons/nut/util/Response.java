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

import java.math.BigDecimal;
import java.util.List;

import org.shredzone.commons.nut.exception.InvalidResponseException;

/**
 * Represents a server response.
 */
public class Response {
    private final String line;
    private final List<String> values;

    /**
     * Creates a {@link Response} from a response line of the server.
     *
     * @param line
     *         Response line of the server, will be parsed and unescaped.
     */
    public Response(String line) {
        this.line = line;
        values = StringUtils.split(line);
        if (values.isEmpty()) {
            throw new InvalidResponseException("Server returned an empty line", getRaw());
        }
    }

    /**
     * Gets a column of the response.
     *
     * @param ix
     *         Column index, starting with 0
     * @return Column value
     * @throws InvalidResponseException
     *         if the response does not have a sufficient number of columns
     */
    public String get(int ix) {
        if (ix >= values.size()) {
            throw new InvalidResponseException("Missing response column " + ix, getRaw());
        }
        return values.get(ix);
    }

    /**
     * Gets a column of the response, as {@link BigDecimal} value.
     *
     * @param ix
     *         Column index, starting with 0
     * @return Column value
     * @throws InvalidResponseException
     *         if the response does not have a sufficient number of columns
     * @throws NumberFormatException
     *         if the column value is not numeric
     */
    public BigDecimal getAsNumber(int ix) {
        return new BigDecimal(get(ix));
    }

    /**
     * Returns all columns of the response.
     */
    public List<String> getAll() {
        return values;
    }

    /**
     * Returns the unparsed response line as received from the server.
     */
    public String getRaw() {
        return line;
    }

    /**
     * Returns the original response line.
     */
    @Override
    public String toString() {
        return getRaw();
    }

}
