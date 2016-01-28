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
 * Converter from objects of {@linkplain SrcType} to {@linkplain DstType}
 * Converter should accept raw src and dest types and then obtain useful information from extended type info.
 * @author Artem Mironov
 */
public interface Converter<SrcType, DstType> {
    /**
     * Converter from {@linkplain SrcType} to {@linkplain DstType}.
     * Implementation should assume following:
     * * {@linkplain SrcType} is assignable from srcType.getRaw()
     * * {@linkplain DstType} is assignable from destType.getRaw()
     * * {@code}src.getClass().isAssignableFrom(srcType) == true{@code}
     * * {@linkplain DstType} class is assignable from returned object
     * @param src source object
     * @param srcType source object type info
     * @param dstType destination object type info
     * @return destination object
     * @throws ConversionException
     */
    DstType convert(final SrcType src, final TypeInfo<SrcType> srcType, final TypeInfo<DstType> dstType) throws ConversionException;
}
