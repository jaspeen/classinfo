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

import org.jeesy.classinfo.ClassInfo;
import org.jeesy.classinfo.ClassInfoScanner;
import org.jeesy.classinfo.PropertyInfo;
import org.jeesy.classinfo.TypeInfo;
import org.jeesy.classinfo.converter.api.ConversionException;
import org.jeesy.classinfo.converter.api.ConversionService;
import org.jeesy.classinfo.converter.api.Converter;

import java.util.Collection;

/**
 * @author Artem Mironov
 */
public class BeanConverter implements Converter<Object, Object> {
    private final ConversionService conversionService;
    public BeanConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Object convert(Object src, TypeInfo<Object> srcType, TypeInfo<Object> dstType) throws ConversionException {
        ClassInfo srcClassInfo = ClassInfoScanner.classInfo(srcType.getRawType());
        ClassInfo dstClassInfo = ClassInfoScanner.classInfo(dstType.getRawType());
        Object dest = null;
        try {
            dest = dstType.getRawType().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        for(PropertyInfo srcPi : (Collection<PropertyInfo>)srcClassInfo.getProperties().values()) {
            PropertyInfo destPi = dstClassInfo.getPropertyInfo(srcPi.getName());
            if(destPi != null) {
                destPi.setValue(dest, conversionService.convertType(srcPi.getValue(src), srcPi.getTypeInfo(), destPi.getTypeInfo()));
            }
        }
        return dest;
    }
}
