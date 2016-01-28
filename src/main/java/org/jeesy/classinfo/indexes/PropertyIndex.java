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
package org.jeesy.classinfo.indexes;

import org.jeesy.classinfo.ClassInfo;
import org.jeesy.classinfo.PropertyInfo;

/**
 * @author Artem Mironov
 */
public interface PropertyIndex extends ClassIndex {
    /**
     * Called by introspector to index property info
     * @param container classinfo contained specified property
     * @param propertyInfo introspected property information
     */
    void index(final ClassInfo container, final PropertyInfo propertyInfo);
}
