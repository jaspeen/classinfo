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
package org.jeesy.classinfo.selector;

import java.util.Map;

/**
 * @author Artem Mironov
 */
public class MappingPropertyHandle<PropType> extends PropertyHandle<PropType, Map> {
    private String key;
    private Class<PropType> propType;

    MappingPropertyHandle(String key, Class<PropType> propType, Map dict) {
        super(null, dict);
        this.key = key;
        this.propType = propType;
    }


    @Override
    public void setValue(Object value) {
        container.put(key, value);
    }
    @Override
    public PropType getValue() {
        return (PropType) container.get(key);
    }
}
