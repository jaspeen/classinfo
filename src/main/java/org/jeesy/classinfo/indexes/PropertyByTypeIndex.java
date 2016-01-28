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
import org.jeesy.classinfo.MethodInfo;
import org.jeesy.classinfo.PropertyInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Index property type -> property info
 * @author Artem Mironov
 */
public class PropertyByTypeIndex implements PropertyIndex {
    private Map<Class, List<PropertyInfo>> storage = new HashMap<>();

    @Override
    public void index(final ClassInfo container, final PropertyInfo propertyInfo) {
        List<PropertyInfo> list = getOrCreateList(propertyInfo.getType());
        list.add(propertyInfo);
    }

    public <T> List<PropertyInfo<T>> getByType(final Class<T> type) {
        return (List<PropertyInfo<T>> )(List<?>)storage.get(type);
    }

    private List<PropertyInfo> getOrCreateList(Class type) {
        List<PropertyInfo> res = storage.get(type);
        if(res == null) {
            res = new ArrayList<>();
            storage.put(type, res);
        }
        return res;
    }
}
