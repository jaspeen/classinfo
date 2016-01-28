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
 * Exception what can be thrown from converters
 */
public class ConversionException extends RuntimeException {
    private TypeInfo<?> srcType;
    private TypeInfo<?> dstType;
    private String msg;

    private String generateMessage(Object value, String message) {
        String srcName = getSrcNodeName();
        String dstName = getDstNodeName();
        StringBuilder sb = new StringBuilder("Failed to convert ");
        if(srcName != null) sb.append("'").append(srcName).append("'");
        sb.append(" value '").append(value).append("' ");
        sb.append("of type ").append(srcType.toString()).append(" to ");
        if(dstName != null) sb.append("'").append(dstName).append("' of ");
        sb.append("type ").append(dstType.toString());
        if(message != null) sb.append(" : ").append(message);
        return sb.toString();
    }

    private static String getNameFromType(TypeInfo<?> type) {
        if(type.getAnnotatedNode() != null && type.getAnnotatedNode().getName() != null)
            return type.getAnnotatedNode().getName();
        else return null;
    }

    public String getSrcNodeName() {
        return getNameFromType(srcType);
    }

    public String getDstNodeName() {
        return getNameFromType(dstType);
    }

    public ConversionException(Object src, TypeInfo<?> srcType, TypeInfo<?> dstType, String message, Throwable cause) {
        super(cause);
        this.srcType = srcType;
        this.dstType = dstType;
        msg = generateMessage(src, message);
    }

    public ConversionException(Object src, TypeInfo<?> srcType, TypeInfo<?> dstType, Throwable cause) {
        this(src, srcType, dstType, null, cause);
    }

    public ConversionException(Object src, TypeInfo<?> srcType, TypeInfo<?> dstType, String message) {
        this(src, srcType, dstType, message, null);
    }

    @Override
    public String getMessage() {
        return msg;
    }
}