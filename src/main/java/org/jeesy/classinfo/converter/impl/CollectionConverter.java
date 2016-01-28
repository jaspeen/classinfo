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
import org.jeesy.classinfo.converter.api.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author Artem Mironov
 */
public class CollectionConverter implements TwoWayConverter<String, Collection> {
    private Class<? extends Collection> defaultImplClass;
    private ConversionService conversionService;
    public CollectionConverter(ConversionService conversionService, Class<? extends Collection> defaultImplClass) {
        this.conversionService = conversionService;
        this.defaultImplClass = defaultImplClass;
    }

    private <T> void fillCollection(Collection<T> c, final Class<T> elementType, final String [] strArray) {
        for(String elm : strArray)
            c.add(conversionService.convertType(elm, TypeInfo.forClass(String.class), TypeInfo.forClass(elementType)));
    }

    protected Collection newInstance() throws Exception {
        return defaultImplClass.newInstance();
    }

    private Class getElementType(Type type) {
        if(type instanceof ParameterizedType) {
            return (Class) ((ParameterizedType)type).getActualTypeArguments()[0];
        } else return String.class;
    }

    @Override
    public Converter<Collection, String> getReverse() {
        return new Converter<Collection, String>() {
            @Override
            public String convert(Collection src, TypeInfo<Collection> srcType, TypeInfo<String> dstType) throws ConversionException {
                StringBuilder out = new StringBuilder();
                Class elementType = getElementType(srcType.getType());
                out.append("[");
                for(Object elm : src) {
                    out.append(conversionService.convertType(elm, TypeInfo.forClass(elementType), (TypeInfo)TypeInfo.forClass(String.class)));
                }
                out.append("]");
                return  out.toString();
            }
        };
    }

    @Override
    public Converter<String, Collection> getDirect() {
        return new Converter<String, Collection>() {
            @Override
            public Collection convert(String src, TypeInfo<String> srcType, TypeInfo<Collection> dstType) throws ConversionException {

                if(Collection.class.isAssignableFrom(dstType.getRawType())) {
                    try {
                        Collection target = newInstance();
                        if (src != null && !src.isEmpty())
                            fillCollection(target, getElementType(dstType.getType()), parseCollection(src));
                        return target;
                    } catch(Exception e) {
                        throw new ConversionException(src, srcType, dstType, e);
                    }
                }
                return null;
            }
        };
    }

    /**
     * Parse string value of format "<value1>"(,"<value2>").*
     * @param value string value
     * @return array with result
     */
    public static String [] parseCollection(final String value) {
        return value.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    //@Override
    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
}
