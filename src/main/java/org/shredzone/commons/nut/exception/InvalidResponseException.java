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
package org.shredzone.commons.nut.exception;

/**
 * This exception is thrown when the server returned an invalid or unexpected response.
 * Reason might be network issues, but also if the server uses a different protocol
 * version.
 */
public class InvalidResponseException extends RuntimeException {
    private final String response;

    public InvalidResponseException(String message, String response) {
        super(message + ": " + response);
        this.response = response;
    }

    /**
     * Returns the response line that caused the error.
     */
    public String getResponse() {
        return response;
    }

}
