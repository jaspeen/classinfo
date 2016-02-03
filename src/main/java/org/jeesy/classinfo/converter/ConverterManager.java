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
package org.jeesy.classinfo.converter;

import org.jeesy.classinfo.TypeInfo;
import org.jeesy.classinfo.converter.api.*;
import org.jeesy.classinfo.converter.impl.ChainableConversionService;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

/**
 * Storage of from/to string conversion setup
 * Can be created for particular area of functionality to convert from/to string on demand.
 * Briefly it have mapping expectedType->converter instance and single fallback converter.
 * Client requests converter by src and dst types via {@link #converterFor(Class, Class)}
 * and if no such converter registered fallback will be returned.
 * {@link DefaultConverter} is the default fallback.
 * @author Artem Mironov
 */
public class ConverterManager extends ChainableConversionService implements ConverterRegistry, Converter<Object, Object>, ConversionService, StringConverter {
    private Map<ConverterKey, Converter> converters = new ConcurrentHashMap<>();

    private ConverterCache byClassCache = new ConverterCache();

    public ConverterManager() {
        super(DefaultConverter.defaultConverter());
    }

    public ConverterManager(ConversionService fallback) {
        super(requireNonNull(fallback));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <SrcType, DstType> Converter<SrcType, DstType> converterFor(Class<SrcType> srcType, Class<DstType> dstType) {
        Converter <SrcType, DstType> res = converters.get(new ConverterKey<>(srcType, dstType));
        if(res == null) {
            return (Converter<SrcType, DstType>) getFallback();
        } else return res;
    }

    @Override
    public <SrcType, DstType> void register(Class<SrcType> srcType, Class<DstType> dstType, Converter<SrcType, DstType> converter) {
        converters.put(new ConverterKey<>(srcType, dstType), converter);
    }

    @Override
    public <SrcType, DstType> void register(Class<SrcType> srcType, Class<DstType> dstType, TwoWayConverter<SrcType, DstType> converter) {
        converters.put(new ConverterKey<>(srcType, dstType), converter.getDirect());
        converters.put(new ConverterKey<>(dstType, srcType), converter.getReverse());
    }

    @Override
    public <SrcType, DstType> void register(Converter<SrcType, DstType> converter) {
        byClassCache.add(converter);
    }


    @SuppressWarnings("unchecked")
    public <I, O> O convert(I src, Class<O> dstType) throws ConversionException {
        if(src == null) return null;
        return (O) convert(src, TypeInfo.forClass((Class)src.getClass()), TypeInfo.forClass((Class)dstType));
    }

    @SuppressWarnings("unchecked")
    @Override
    public<I, O> O convertType(I src, TypeInfo<I> srcType, TypeInfo<O> dstType) throws ConversionException {
        return (O) convert(src, (TypeInfo)srcType, (TypeInfo)dstType);
    }

    @Override
    public Object convert(Object src, TypeInfo<Object> srcType, TypeInfo<Object> dstType) throws ConversionException {
        Converter<Object, Object> converter = converterFor(srcType.getRawType(), dstType.getRawType());
        return converter.convert(src, srcType, dstType);
    }

    private static class ConverterKey<SrcType, DstType> {
        Class<SrcType> srcType;
        Class<DstType> dstType;

        public ConverterKey(Class<SrcType> srcType, Class<DstType> dstType) {
            this.srcType = srcType;
            this.dstType = dstType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ConverterKey)) return false;
            ConverterKey<?, ?> that = (ConverterKey<?, ?>) o;
            return Objects.equals(srcType, that.srcType) &&
                    Objects.equals(dstType, that.dstType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(srcType, dstType);
        }
    }

    /**
     * Get converter instance by converter class. Uses new instance with default constructor.
     * @param converterClass Converter class to instantiate
     * @param <T> generic converter target type
     * @return new or cached converter instance
     */
    @Override
    public <I, O, T extends Converter<I, O>> T converterByType(final Class<T> converterClass) {
        T converterInstance = byClassCache.getConverter(this, converterClass);
        return converterInstance;
    }

    public <T> T fromString(final TypeInfo<T> type, final String value)
            throws ConversionException {
        return convertType(value, TypeInfo.forClass(String.class), type);
    }

    public <T> String toString(TypeInfo<T> type, final T value) throws ConversionException {
        return (String) convert(value, (TypeInfo)type, (TypeInfo)TypeInfo.forClass(String.class));
    }
}
