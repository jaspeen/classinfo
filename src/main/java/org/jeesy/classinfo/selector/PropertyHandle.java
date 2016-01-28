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

import org.jeesy.classinfo.PropertyInfo;

/**
 * Property info together with container instance.
 * @author Artem Mironov
 */
public class PropertyHandle<PropType, ContainerType> {
    protected final PropertyInfo<PropType> info;
    protected final ContainerType container;

    protected PropertyHandle(PropertyInfo<PropType> info, ContainerType container) {
        this.info = info;
        this.container = container;
    }

    public PropertyInfo<PropType> getInfo() {return info;}
    public void setValue(PropType value) {info.setValue(container, value);}
    public PropType getValue() {return info.getValue(container);}
    public ContainerType getContainer() {return container;}
}
