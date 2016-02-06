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

import org.jeesy.classinfo.Utils;
import org.jeesy.classinfo.converter.api.ConversionService;
import org.jeesy.classinfo.converter.api.Converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Artem Mironov
 */
public class ConverterCache {

    private final Map<Class, Converter> converters = new ConcurrentHashMap<>();

    public <T extends Converter> T getConverter(ConverterManager holder, Class<T> converterClass) {
        T c = (T) converters.get(converterClass);
        if(c == null) {
            try {
                Constructor<T> constructor = null;
                try {
                    constructor = converterClass.getConstructor(ConversionService.class);
                    c = constructor.newInstance(holder);
                } catch (NoSuchMethodException e) {
                    constructor = converterClass.getConstructor();
                    c = constructor.newInstance();
                }
                converters.put(converterClass, c);
            } catch(InvocationTargetException e) {
                Utils.uncheckedThrow(e.getTargetException());
            } catch (NoSuchMethodException|IllegalAccessException|InstantiationException e) {
                Utils.uncheckedThrow(e);
            }
        }
        return c;
    }

    public void add(Converter<?, ?> converter) {
        converters.put(converter.getClass(), converter);
    }
}
