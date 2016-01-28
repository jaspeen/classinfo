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

import org.jeesy.classinfo.AnnotatedNode;
import org.jeesy.classinfo.TypeInfo;
import org.jeesy.classinfo.converter.annotations.Format;
import org.jeesy.classinfo.converter.api.ConversionException;
import org.jeesy.classinfo.converter.api.Converter;
import org.jeesy.classinfo.converter.api.TwoWayConverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converters for simple standard java types
 * @author Artem Mironov
 */
public class StandardConverters {

    public static class SimpleToStringConverter<T> implements Converter<T,String> {

        @Override
        public String convert(T src, TypeInfo<T> srcType, TypeInfo<String> dstType) throws ConversionException {
            return  String.valueOf(src);
        }
    }

    public static class NumberToStringConverter<T extends Number> implements Converter<T, String> {
        @Override
        public String convert(T src, TypeInfo<T> srcType, TypeInfo<String> dstType) throws ConversionException {
            //TODO: use Format
            return src != null ? src.toString() : null;
        }
    }

    public static abstract class StringToNumberConverter<T extends Number> implements Converter<String, T> {
        protected abstract T fromString(final String value, TypeInfo<T> type);
        protected final T defaultValue;
        public StringToNumberConverter(T defaultValue) {this.defaultValue = defaultValue;}
        @Override
        public T convert(String src, TypeInfo<String> srcType, TypeInfo<T> dstType) throws ConversionException {
            if (src == null || src.isEmpty()) {
                return defaultValue;
            } else {
                try {
                    return  fromString(src, dstType);
                } catch(Exception e) {
                    throw new ConversionException(src, srcType, dstType, e);
                }
            }
        }
    }

    public static class IntConverter extends StringToNumberConverter<Integer> {
        public IntConverter(final Integer defaultValue) {
            super(defaultValue);
        }

        @Override
        protected Integer fromString(final String value, TypeInfo<Integer> type) {
            return Integer.valueOf(value);
        }
    }

    public static class ShortConverter extends StringToNumberConverter<Short> {
        public ShortConverter(final Short defaultValue) {
            super(defaultValue);
        }

        @Override
        protected Short fromString(final String value, TypeInfo<Short> type) {
            return Short.valueOf(value);
        }
    }

    public static class ByteConverter extends StringToNumberConverter<Byte> {
        public ByteConverter(final Byte defaultValue) {
            super(defaultValue);
        }

        @Override
        protected Byte fromString(final String value, TypeInfo<Byte> type) {
            return Byte.parseByte(value);
        }
    }

    public static class LongConverter extends StringToNumberConverter<Long> {
        public LongConverter(final Long defaultValue) {
            super(defaultValue);
        }

        @Override
        protected Long fromString(final String value, TypeInfo<Long> type) {
            return Long.parseLong(value);
        }
    }

    public static class FloatConverter extends StringToNumberConverter<Float> {
        public FloatConverter(final Float defaultValue) {
            super(defaultValue);
        }

        @Override
        protected Float fromString(final String value, TypeInfo<Float> type) {
            return Float.parseFloat(value);
        }
    }

    public static class DoubleConverter extends StringToNumberConverter<Double> {
        public DoubleConverter(final Double defaultValue) {
            super(defaultValue);
        }

        @Override
        protected Double fromString(final String value, TypeInfo<Double> type) {
            return Double.parseDouble(value);
        }
    }

    public static class BigDecimalConverter extends StringToNumberConverter<BigDecimal> {
        public BigDecimalConverter(final BigDecimal defaultValue) {
            super(defaultValue);
        }

        @Override
        protected BigDecimal fromString(final String value, TypeInfo<BigDecimal> type) {
            if(value == null || value.isEmpty()) {
                return defaultValue;
            } else {
                return new BigDecimal(value);
            }
        }
    }

    public static class BigIntegerConverter extends StringToNumberConverter<BigInteger> {
        public BigIntegerConverter(final BigInteger defaultValue) {
            super(defaultValue);
        }
        @Override
        protected BigInteger fromString(final String value, TypeInfo<BigInteger> type) {
            if(value == null || value.isEmpty()) {
                return defaultValue;
            } else {
                return new BigInteger(value);
            }
        }
    }

    public static class DateStringConverter implements TwoWayConverter<String, Date> {
        public static final String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
        private static DateFormat createFormat(TypeInfo<?> typeInfo) {
            AnnotatedNode an = typeInfo.getAnnotatedNode();
            String format = DEFAULT_FORMAT;
            if(an != null) {
                Format fmt = an.getAnnotation(Format.class);
                if(fmt != null && !fmt.value().isEmpty()) {
                    format = fmt.value();
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            return sdf;
        }
        @Override
        public Converter<String, Date> getDirect() {
            return new Converter<String, Date>() {
                @Override
                public  Date convert(String src, TypeInfo<String> srcType, TypeInfo<Date> dstType) throws ConversionException {
                    if(src == null || src.isEmpty()) return null;
                    try {
                        return createFormat(dstType).parse(src);
                    } catch (ParseException e) {
                        throw new ConversionException(src, srcType, dstType, e);
                    }
                }
            };
        }

        @Override
        public Converter<Date, String> getReverse() {
            return new Converter<Date, String>() {
                @Override
                public String convert(Date src, TypeInfo<Date> srcType, TypeInfo<String> dstType) throws ConversionException {
                    if(src == null) return null;
                    return createFormat(srcType).format(src);
                }
            };
        }
    }

    public static class BooleanConverter implements Converter<String, Boolean> {

        @Override
        public Boolean convert(String src, TypeInfo<String> srcType, TypeInfo<Boolean> dstType) throws ConversionException {
            return Boolean.parseBoolean(src);
        }
    }


}
