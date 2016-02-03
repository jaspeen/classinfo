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

import org.jeesy.classinfo.ClassInfo;
import org.jeesy.classinfo.ClassInfoScanner;
import org.jeesy.classinfo.PropertyInfo;

import java.util.List;

/**
 * Resolve property selector against some instance.
 * @author Artem Mironov
 */
public class PropertyHandle<ContainerType, PropType> {
    protected final PropertyInfo<PropType> info;
    protected final ContainerType container;
    protected InstantiationBehaviour instantiationBehaviour;

    protected PropertyHandle(PropertyInfo<PropType> info, ContainerType container, InstantiationBehaviour instantiationBehaviour) {
        this.info = info;
        this.container = container;
        this.instantiationBehaviour = instantiationBehaviour;
    }

    public PropertyInfo<PropType> getInfo() {return info;}
    public void setValue(PropType value) {info.setValue(container, value);}
    public PropType getValue() {return info.getValue(container);}
    public ContainerType getContainer() {return container;}
    public PropertyHandle select(List<String> path) {
        if(path.isEmpty()) return this;
        ClassInfo pci = info.getClassInfo();
        if(pci == null) throw new RuntimeException("No property with name "+path.get(0)+" exists");
        PropertyInfo nestedPi = pci.getPropertyInfo(path.get(0));
        if(nestedPi == null) throw new RuntimeException("No property with name "+path.get(0)+" exists");;
        PropType val = getValue();
        if(val == null) {
            if(instantiationBehaviour == null) throw new RuntimeException("element "+path.get(0)+" is null on path");
            val = instantiationBehaviour.newInstance(info.getType(), info);
            setValue(val);
        }
        return new PropertyHandle(nestedPi, val, instantiationBehaviour).select(path.subList(1, path.size()));
    }
}
