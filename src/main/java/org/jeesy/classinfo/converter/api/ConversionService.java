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
 * Converter what able to convert multiple types depending on parameters
 * @author Artem Mironov
 */
public interface ConversionService extends Converter<Object, Object> {
    /**
     * Convert source value of specified type to value of destination type
     * @param src source value
     * @param srcType extended source type
     * @param dstType destination type
     * @return converted value of type which can be assigned to dstType
     * @throws ConversionException
     */
    <I, O> O convertType(I src, TypeInfo<I> srcType, TypeInfo<O> dstType) throws ConversionException;

    /**
     * Search for converter of specified type.
     * Optionally if service is capable of creating converter instance it can return new converter if nothing found
     * @param converterType exact type of the requested converter
     * @param <I> type of the source what converter supports
     * @param <O>type of the destination what converter supports
     * @return cached or new converter instance or null
     */
    <I, O, T extends Converter<I, O>> T converterByType(Class<T> converterType);

    /**
     * Lookup exact converter for the specified raw types.
     * @param srcType source type
     * @param dstType destination type
     * @return converter what can converter values of source type to destination type
     */
    <SrcType, DstType> Converter<SrcType, DstType> converterFor(Class<SrcType> srcType, Class<DstType> dstType);
}
