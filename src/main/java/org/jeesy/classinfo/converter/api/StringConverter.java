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
package org.jeesy.classinfo.converter.api;

import org.jeesy.classinfo.TypeInfo;

/**
 * From/to string converter
 */
public interface StringConverter {
    /**
     * Create new instance of expected type from string value
     * @param type Expected target type
     * @param value String value to convert
     * @return instance of expected type or child
     * @throws ConversionException if something goes wrong
     */
    <T> T fromString(final TypeInfo<T> type, final String value) throws ConversionException;

    /**
     * Convert object to string. It's desirable what result can be parsed back
     * and obj.equals(fromString(*,ojb.getClass(),toString(*,obj))) == true
     * @param type type info of value
     * @param value Object to convert  @return string
     * @throws ConversionException if something goes wrong
     */
    <T> String toString(TypeInfo<T> type, final T value) throws ConversionException;

    /**
     * Abstract converter which requires to implement only {@link #fromString(TypeInfo, String)}
     * since {@link #toString(TypeInfo, Object)} implemented via {@link String#valueOf(Object)}
     */
    abstract class FromStringConverter implements StringConverter {
        @Override
        public <T> String toString(TypeInfo<T> type, T value) throws ConversionException {
            return String.valueOf(value);
        }
    }
}