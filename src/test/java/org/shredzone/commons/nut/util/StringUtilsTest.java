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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void split() {
        assertThat(StringUtils.split(""))
                .isEmpty();
        assertThat(StringUtils.split("         "))
                .containsExactly(" ");
        assertThat(StringUtils.split("CMDDESC     su700   load.on         "))
                .containsExactly("CMDDESC", "su700", "load.on");
        assertThat(StringUtils.split("CMDDESC su700 load.on \"Turn on the load immediately\""))
                .containsExactly("CMDDESC", "su700", "load.on", "Turn on the load immediately");
        assertThat(StringUtils.split("CMDDESC \"Turn on \\ the \\\"load\\\" immediately\""))
                .containsExactly("CMDDESC", "Turn on \\ the \"load\" immediately");
        assertThat(StringUtils.split("\"CMDDESC\" \"su700\" \"load.on\""))
                .containsExactly("CMDDESC", "su700", "load.on");
    }

    @Test
    public void quote() {
        assertThat(StringUtils.quote(""))
                .isEqualTo("");
        assertThat(StringUtils.quote("        "))
                .isEqualTo("\"        \"");
        assertThat(StringUtils.quote("ABC123"))
                .isEqualTo("ABC123");
        assertThat(StringUtils.quote("ABC\\123"))
                .isEqualTo("\"ABC\\\\123\"");
        assertThat(StringUtils.quote("ABC\"foo\"123"))
                .isEqualTo("\"ABC\\\"foo\\\"123\"");
        assertThat(StringUtils.quote("\"a string\""))
                .isEqualTo("\"\\\"a string\\\"\"");
    }

    @Test
    public void unquote() {
        assertThat(StringUtils.unquote(""))
                .isEqualTo("");
        assertThat(StringUtils.unquote("\"        \""))
                .isEqualTo("        ");
        assertThat(StringUtils.unquote("ABC123"))
                .isEqualTo("ABC123");
        assertThat(StringUtils.unquote("\"ABC\\\\123\""))
                .isEqualTo("ABC\\123");
        assertThat(StringUtils.unquote("\"ABC\\\"foo\\\"123\""))
                .isEqualTo("ABC\"foo\"123");
        assertThat(StringUtils.unquote("\"\\\"a string\\\"\""))
                .isEqualTo("\"a string\"");
    }

}
