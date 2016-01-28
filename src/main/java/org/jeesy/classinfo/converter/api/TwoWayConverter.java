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

/**
 * Converter which can convert types in both directions.
 *
 * @author Artem Mironov
 */
public interface TwoWayConverter<SrcType, DstType> {
    /**
     * Returns converter from {@linkplain SrcType} to {@linkplain DstType}
     */
    Converter<SrcType, DstType> getDirect();

    /**
     * Returns converter from {@linkplain DstType} to {@linkplain SrcType}
     */
    Converter<DstType, SrcType> getReverse();
}
