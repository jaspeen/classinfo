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
 * All methods what returns selector returns a new instance
 * @author Artem Mironov
 */
public class PropertySelector {
    private List<String> path;
    private boolean createIfNull = false;
    private InstantiationBehaviour instantiationBehaviour = new ClassInfoScanner.DefaultInstantiationBehaviour();

    protected PropertySelector(List<String> path) {
        this.path = path;
        instantiationBehaviour = new ClassInfoScanner.DefaultInstantiationBehaviour();
    }

    /**
     * Return new selector by relative path starting from current
     * @param path relative path
     */
    public PropertySelector nested(String path) {
        PropertySelector nested =  new PropertySelector(this.path);
        nested.path.addAll(Arrays.asList(path.split("\\.")));
        nested.createIfNull = createIfNull;
        nested.instantiationBehaviour = instantiationBehaviour;
        return nested;
    }

    public PropertySelector createNullElements() {
        createIfNull = true;
        instantiationBehaviour = new ClassInfoScanner.DefaultInstantiationBehaviour();
        return this;
    }

    public PropertySelector createNullElements(InstantiationBehaviour instantiationBehaviour) {
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

    public PropertyHandle resolve(Object bean) {
        if(path == null) path = Collections.emptyList();
        if(path.isEmpty()) {return null;};
        ClassInfo classInfo = ClassInfoScanner.classInfo(bean.getClass());
        String pathElm = path.get(0);
        PropertyInfo pi = classInfo.getPropertyInfo(pathElm);
        if(pi == null) throw new RuntimeException("Property "+pathElm+" is null on path");
        PropertyHandle propertyHandle = new PropertyHandle(pi, bean, createIfNull ? instantiationBehaviour : null);
        return propertyHandle.select(path.subList(1, path.size()));
    }

    public static PropertySelector parse(String path) {
        return new PropertySelector(new ArrayList<>(Arrays.asList(path.split("\\."))));
    }

    public InstantiationBehaviour getInstantiationBehaviour() {
        return instantiationBehaviour;
    }
}
