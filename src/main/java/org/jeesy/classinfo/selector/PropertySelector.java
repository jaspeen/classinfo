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

import java.util.*;

/**
 * @author Artem Mironov
 */
public class PropertySelector {
    private List<String> path;
    private boolean createIfNull = false;
    private InstantiationBehaviour instantiationBehaviour = new ClassInfoScanner.DefaultInstantiationBehaviour();
    private Object bean;

    protected PropertySelector(Object bean) {
        this.bean = bean;
        instantiationBehaviour = new ClassInfoScanner.DefaultInstantiationBehaviour();
    }

    public PropertySelector withPath(String ... path) {
        this.path = Arrays.asList(path);
        return this;
    }

    public PropertySelector createIfNull() {
        this.createIfNull = true;
        instantiationBehaviour = new ClassInfoScanner.DefaultInstantiationBehaviour();
        return this;
    }

    public PropertySelector createIfNull(InstantiationBehaviour instantiationBehaviour) {
        createIfNull = true;
        this.instantiationBehaviour = instantiationBehaviour;
        return this;
    }

    protected void selectByPath(List<PropertyHandle> handles, List<String> tmpPath, PropertyInfo info) {

    }

    protected PropertyHandle onProperty(String prop, PropertyHandle current) {
        return null;
    }

    protected PropertyHandle onMapping(String mappingName, PropertyHandle current) {
        return null;
    }

    protected PropertyHandle onIndexed(int index, PropertyHandle current) {
        return null;
    }


    protected void selectProperties(List<PropertyHandle> handles, ClassInfo classInfo) {

    }

    public PropertyHandle select(ClassInfo classInfo) {
        if(path == null) path = Collections.emptyList();
        if(path.isEmpty()) {return null;};

        String pathElm = path.get(0);
        PropertyInfo pi = classInfo.getPropertyInfo(pathElm);
        if(pi == null) return null;
        PropertyHandle propertyHandle = new PropertyHandle(pi, bean);
        for(int i = 1; i<path.size(); i++) {
            pathElm = path.get(i);
            propertyHandle = onProperty(pathElm, propertyHandle);
            if(propertyHandle == null)
                return null;
        }

        return propertyHandle;
    }

    public static PropertySelector on(Object obj) {
        return new PropertySelector(obj);
    }

}
