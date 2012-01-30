/**
 * Copyright (C) 2011 Jacob Scott <jascottytechie@gmail.com>
 * Description: static random methods
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jascotty2;

import java.util.Date;
import java.util.Random;

public class Rand {

    static Random rand = new Random();
    public static char filenameChars[] = {};
    protected static boolean isRand = false;

    static String randFname() {
        return randFname(10, 25);
    }
    
    static String randFname(int length) {
        return randFname(length, length);
    }

    static String randFname(int minlength, int maxlength) {
        if (filenameChars.length == 0) {
            // populate with alphanumerical chars
            filenameChars = new char[62];
            int n = 48;
            for (int i = 0; i < 62; ++i) {
                filenameChars[i] = (char) n++;
                if (n == 58) {
                    n = 65;
                } else if (n == 91) {
                    n = 97;
                }
            }
        }
        String ret = "";
        for (int i = RandomInt(minlength, maxlength); i > 0; --i) {
            ret += filenameChars[RandomInt(0, filenameChars.length - 1)];
        }
        return ret;
    }

    public static int RandomInt(int min, int max) {
        if (min == max) {
            return min;
        }
        if (max < min) {
            return RandomInt(max, min);
        }
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return min + rand.nextInt(max - min + 1);
    }

    public static double RandomDouble() {
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return rand.nextDouble();
    }

    public static double RandomDouble(double min, double max) {
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return min + rand.nextDouble() * (max - min);
    }

    public static boolean RandomBoolean() {
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return rand.nextBoolean();
    }

    public static boolean RandomBoolean(double chance) {
        if (chance >= 1) {
            return true;
        }
        if (chance <= 0) {
            return false;
        }
        if (!isRand) {
            rand.setSeed((new Date()).getTime());
            isRand = true;
        }
        return rand.nextDouble() <= chance;
    }
} // end class Rand

