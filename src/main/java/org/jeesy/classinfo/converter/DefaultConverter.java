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

import org.jeesy.classinfo.converter.api.*;
import org.jeesy.classinfo.converter.impl.CollectionConverter;
import org.jeesy.classinfo.converter.impl.DefaultFallback;


import static org.jeesy.classinfo.converter.impl.StandardConverters.*;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;


/**
 * Default converter what covers mostly all simple values like numbers, boolean, date, collections.
 * For unknown types it uses reflection instantiation via constructor with single string argument.
 * @author Artem Mironov
 */
@SuppressWarnings("unchecked")
public class DefaultConverter extends ConverterManager {
    boolean readOnly = false;
    protected DefaultConverter(ConversionService fallback) {
        super(fallback);
        registerDefaults();
    }
    private DefaultConverter() {
        fallback = new DefaultFallback<>(this);
        registerDefaults();
        readOnly = true;
    }
    public static <T extends ConverterRegistry & ConversionService> void registerBasicConverters(T manager) {
        registerFromString(manager, int.class, new IntConverter(0));
        registerFromString(manager, Integer.class, new IntConverter(null));
        registerFromString(manager, short.class, new ShortConverter((short)0));
        registerFromString(manager, Short.class, new ShortConverter(null));
        registerFromString(manager, byte.class, new ByteConverter((byte) 0));
        registerFromString(manager, Byte.class, new ByteConverter(null));
        registerFromString(manager, long.class, new LongConverter(0L));
        registerFromString(manager, Long.class, new LongConverter(null));
        registerFromString(manager, float.class, new FloatConverter(0F));
        registerFromString(manager, Float.class, new FloatConverter(null));
        registerFromString(manager, double.class, new DoubleConverter(0D));
        registerFromString(manager, Double.class, new DoubleConverter(null));
        registerFromString(manager, boolean.class, new BooleanConverter());
        registerFromString(manager, Boolean.class, new BooleanConverter());
        registerFromString(manager, BigDecimal.class, new BigDecimalConverter(null));
        registerFromString(manager, BigInteger.class, new BigIntegerConverter(null));
        registerFromString(manager, Date.class, new DateStringConverter());
    }

    /**
     * This method should be overriden
     */
    public static <T extends ConverterRegistry & ConversionService> void registerDefaultCollectionsConverters(T manager) {
        registerFromString(manager,Set.class, new CollectionConverter(manager, HashSet.class));
        registerFromString(manager,SortedSet.class, new CollectionConverter(manager, TreeSet.class));
        registerFromString(manager,NavigableSet.class, new CollectionConverter(manager, TreeSet.class));
        registerFromString(manager,List.class, new CollectionConverter(manager, ArrayList.class));
        registerFromString(manager,Collection.class, new CollectionConverter(manager, ArrayList.class));
        registerFromString(manager,Queue.class, new CollectionConverter(manager, ArrayDeque.class));
        registerFromString(manager,Deque.class, new CollectionConverter(manager, ArrayDeque.class));
    }

    private static <ToType> void registerFromString(ConverterRegistry manager, Class<ToType> toClass, Converter<String, ToType> converter) {
        manager.register(String.class, toClass, converter);
        manager.register(toClass, String.class, new SimpleToStringConverter<ToType>());
    }

    private static <ToType> void registerFromString(ConverterRegistry manager, Class<? extends ToType> toClass, TwoWayConverter<String, ToType> converter) {
        manager.register(String.class, (Class<ToType>)toClass, converter);
    }

    private DefaultConverter registerDefaults() {
        registerBasicConverters(this);
        registerDefaultCollectionsConverters(this);
        return this;
    }

    @Override
    public <SrcType, DstType> void register(Class<SrcType> srcType, Class<DstType> dstType, Converter<SrcType, DstType> converter) {
        if(readOnly) throw new UnsupportedOperationException("Converter manager is readonly");
        super.register(srcType, dstType, converter);
    }

    @Override
    public <SrcType, DstType> void register(Class<SrcType> srcType, Class<DstType> dstType, TwoWayConverter<SrcType, DstType> converter) {
        if(readOnly) throw new UnsupportedOperationException("Converter manager is readonly");
        super.register(srcType, dstType, converter);
    }

    private static final DefaultConverter INSTANCE = new DefaultConverter();

    /**
     * Global immutable default converter instance
     */
    public static DefaultConverter defaultConverter() {return INSTANCE;}
}
