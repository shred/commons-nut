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

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some general String methods related to the NUT protocol.
 */
public final class StringUtils {
    private static final Pattern PARAMETER = Pattern.compile("([^\"]\\S*|\".+?(?<!\\\\)\")\\s*");

    private static final Pattern QUOTABLE_CHARS = Pattern.compile("[\\s\"\\\\]");
    private static final Pattern QUOTED_TEXT = Pattern.compile("\"(.*)\"");

    private StringUtils() {
        // Utility class without constructor
    }

    /**
     * Splits a string into columns. Columns in double quotes are unqouted and unescaped.
     *
     * @param str
     *         String to split
     * @return Columns that were found
     */
    public static List<String> split(String str) {
        return PARAMETER.matcher(str).results()
                .map(m -> m.group(1))
                .map(StringUtils::unquote)
                .collect(toUnmodifiableList());
    }

    /**
     * Automatically quotes a string if quoting is required.
     * <p>
     * Quoting is required if the string contains a whitespace character, a double quote,
     * or a backslash.
     *
     * @param str
     *         String to quote
     * @return Quoted string, or the original string if no quoting was necessary.
     */
    public static String quote(String str) {
        if (QUOTABLE_CHARS.matcher(str).find()) {
            return "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
        return str;
    }

    /**
     * Automatically unquotes a quoted string.
     * <p>
     * Unquoting and unescaping takes place if the string was surrounded by double
     * quotes.
     *
     * @param str
     *         String to unqoute
     * @return Unquoted string, or the original string if it was not quoted.
     */
    public static String unquote(String str) {
        Matcher m = QUOTED_TEXT.matcher(str);
        if (m.matches()) {
            return m.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return str;
    }

}
