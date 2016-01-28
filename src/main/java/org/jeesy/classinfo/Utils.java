/*
 * Copyright 2015 Artem Mironov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jeesy.classinfo;

/**
 *
 * @author Artem Mironov
 */
class Utils {
    private Utils(){}
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void throwException(Throwable exception, Object dummy) throws T
    {
        throw (T) exception;
    }

    /**
     * Hack to throw checked exception as is from method what not declares it
     * @param exception
     */
    public static void uncheckedThrow(Throwable exception)
    {
        Utils.<RuntimeException>throwException(exception, null);
    }

    /**
     * Analog of method from guava Preconditions
     * @param condition if false exception will be thrown
     * @param message template for exception message
     * @param args template arguments
     */
    public static void checkArgument(boolean condition, String message, Object ... args) {
        if(!condition) throw new IllegalArgumentException(String.format(message, args));
    }


    public static String decapitalize(String val) {
        if (val == null || val.length() == 0) {
            return val;
        }
        if (val.length() > 1 && Character.isUpperCase(val.charAt(1)) &&
                Character.isUpperCase(val.charAt(0))){
            return val;
        }
        char chars[] = val.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public static String capitalize(String val) {
        if (val == null || val.length() == 0) {
            return val;
        }
        if (val.length() > 0 && Character.isUpperCase(val.charAt(0))){
            return val;
        }
        char chars[] = val.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }


}
