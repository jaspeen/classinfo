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
package org.jeesy.classinfo.converter.impl;

import org.jeesy.classinfo.TypeInfo;
import org.jeesy.classinfo.converter.api.ConversionException;
import org.jeesy.classinfo.converter.api.ConversionService;
import org.jeesy.classinfo.converter.api.Converter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

/**
 * @author Artem Mironov
 */
public class DefaultFallback<SrcType, DstType> implements Converter<SrcType, DstType>, ConversionService {

    private ConversionService conversionService;

    public DefaultFallback(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public DstType convert(SrcType src, TypeInfo<SrcType> srcType, TypeInfo<DstType> dstType) throws ConversionException {
        Class<DstType> rawType = dstType.getRawType();
        if (srcType.getRawType().equals(rawType)) return (DstType) src;
        //src is a string

        DstType res = null;
        if(CharSequence.class.isAssignableFrom(srcType.getRawType())) {
            String val;
            if(String.class.equals(srcType.getRawType()))
                val = (String) src;
            else
                val = src.toString();
            res = fromString(val, dstType);
        }
        if(res == null){
            if(rawType.equals(String.class)) {
                return (DstType) src.toString();
            }
            //try constructor with 1 argument and dstType
            try {
                Constructor<DstType> constructor = rawType.getConstructor(srcType.getRawType());
                boolean accesible = constructor.isAccessible();
                constructor.setAccessible(true);
                res = constructor.newInstance(src);
                constructor.setAccessible(accesible);
            } catch (Exception e) {
                throw new ConversionException(src, srcType, dstType,"Cannot create target with converting constructor", e);
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    private DstType fromString(String src, TypeInfo<DstType> type) {
        Class<DstType> rawType = type.getRawType();
        if(rawType.isEnum()) {
            try {
                return (DstType) Enum.valueOf((Class<? extends Enum>) rawType, src);
            } catch(IllegalArgumentException e) {
                throw new ConversionException(src, TypeInfo.forClass(String.class), type, e);
            }
        } else if(rawType.isArray()) {
            if(rawType.getComponentType().equals(String.class))
                return (DstType) CollectionConverter.parseCollection(src);
            else {
                return (DstType) fillArray(rawType.getComponentType(), CollectionConverter.parseCollection(src));

            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T [] fillArray(final Class componentType, final String [] strArray) {
        T [] result = (T[]) Array.newInstance(componentType, strArray.length);
        for(int i = 0; i<result.length; i++) {
            result[i] = (T) conversionService.convertType(strArray[i], (TypeInfo)TypeInfo.forClass(String.class), TypeInfo.forClass(componentType));
        }
        return result;
    }

    @Override
    public <I, O> O convertType(I src, TypeInfo<I> srcType, TypeInfo<O> dstType) throws ConversionException {
        return ((Converter<I,O>)this).convert(src, srcType, dstType);
    }

    @Override
    public <I, O, T extends Converter<I, O>> T lookupConverter(Class<T> converterType) {
        //actually return nothing because this is not capable of cache any converter
        return null;
    }

    public static DefaultFallback newInstance(ConversionService conversionService) {
        return new DefaultFallback(conversionService);
    }
}
