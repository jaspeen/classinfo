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
package org.jeesy.classinfo;

import org.jeesy.classinfo.indexes.ClassIndex;
import org.jeesy.classinfo.indexes.CommonClassIndex;
import org.jeesy.classinfo.indexes.PropertyIndex;
import org.jeesy.classinfo.selector.PropertyHandle;
import org.jeesy.classinfo.selector.PropertySelector;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.jeesy.classinfo.Utils.decapitalize;

/**
 * Holds class metadata like javabean properties, annotations and custom indexes.
 * @author Artem Mironov
 */
@SuppressWarnings("unchecked")
public class ClassInfo<ClassType> implements AnnotatedNode {
    private static final Logger LOG = Logger.getLogger(ClassInfo.class.getCanonicalName());
    private final ClassInfoScanner scanner;
    private final Class<ClassType> type;
    private final Map<String, PropertyInfo> properties;
    private final Map<Class<? extends ClassIndex>, ClassIndex> scanIndexMap;
    private final ClassInfo parent;

    private final static String PREFIX_GET = "get";
    private final static String PREFIX_SET = "set";
    private final static String PREFIX_IS = "is";

    ClassInfo(final Class<ClassType> clazz, final ClassInfo parent, final ClassInfoScanner scanner) {
        type = clazz;
        this.scanner = scanner;
        this.parent = parent;
        properties = new HashMap<>();
        scanIndexMap = new ConcurrentHashMap<>();
    }
    public PropertyInfo getPropertyInfo(final String propertyName) {
        return properties.get(propertyName);
    }

    public <T> PropertyInfo<T> getPropertyInfo(final String propertyName, final Class<T> propertyType) {
        PropertyInfo<T> pi = properties.get(propertyName);
        if(pi == null) return null;
        if(pi.type.isAssignableFrom(propertyType))
            return pi;
        else
            throw new IllegalArgumentException("Property "+propertyName+" is not assignable from class "+propertyType);
    }

    public Class<ClassType> getType() {
        return type;
    }

    public ClassInfo getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return type.getName();
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
        return type.getAnnotation(annotationClass);
    }

    @Override
    public boolean hasAnnotation(final Class<? extends Annotation> annotationClass) {
        return type.isAnnotationPresent(annotationClass);
    }

    @Override
    public Collection<Annotation> getAnnotataions() {
        return Arrays.asList(type.getAnnotations());
    }

    public <T extends ClassIndex> T getIndex(final Class<T> indexType) {
        T index = (T) scanIndexMap.get(indexType);
        if(index == null) {
            index = createIndex(indexType);
            if(index instanceof PropertyIndex) {
                for (PropertyInfo<?> pi : properties.values())
                    ((PropertyIndex) index).index(this, pi);
            }

            if(index instanceof CommonClassIndex) {
                ((CommonClassIndex) index).index(this);
            }
            scanIndexMap.put(indexType, index);
        }
        return index;
    }

    private <T extends ClassIndex> T createIndex(Class<T> indexType) {
        try {
            return scanner.getInstantiationBehaviour().newInstance(indexType, null);
        } catch (Throwable e) {
            Utils.uncheckedThrow(e);
        }
        return null;
    }

    private <T extends ClassIndex> T getOrCreateIndex(Class<T> indexType) {
        T res = (T) scanIndexMap.get(indexType);
        if(res == null) {
             res = createIndex(indexType);
             scanIndexMap.put(indexType, res);
        }
        return res;
    }

    private void indexProperty(PropertyInfo info) {
        for (Class<? extends ClassIndex> indexType : scanner.getScanIndexes()) {
            if (PropertyIndex.class.isAssignableFrom(indexType))
                getOrCreateIndex((Class<PropertyIndex>) indexType).index(this, info);
        }
    }

    private void indexAfter() {
        for(Class<? extends ClassIndex> indexType : scanner.getScanIndexes()) {
            if(!CommonClassIndex.class.isAssignableFrom(indexType))
                getOrCreateIndex((Class<CommonClassIndex>)indexType).index(this);
        }
    }

    private static String getPropertyName(Method method) {
        String methodName = method.getName();
        if (methodName.startsWith(PREFIX_SET) && methodName.length() > 3 && method.getParameterTypes().length == 1 && method.getReturnType() == Void.TYPE)
            return decapitalize(methodName.substring(PREFIX_SET.length()));
        else if (methodName.startsWith(PREFIX_GET) && methodName.length() > 3 && method.getParameterTypes().length == 0 && method.getReturnType() != Void.TYPE)
            return decapitalize(methodName.substring(PREFIX_GET.length()));
        else if (methodName.startsWith(PREFIX_IS) && methodName.length() > 2 && method.getParameterTypes().length == 0 && method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class)
            return decapitalize(methodName.substring(PREFIX_IS.length()));
        else
            return null;
    }

    private boolean isGetter(Method m) {
        return m.getName().startsWith(PREFIX_GET) || m.getName().startsWith(PREFIX_IS);
    }

    public Map<String, PropertyInfo> getProperties() {
        return properties;
    }

    private static void collectAnnotations(PropertyInfo propertyInfo, AnnotatedElement annotatedElement, ClassInfoScanner.ScanBehaviour scanBehaviour) {
        if(annotatedElement != null) {
            for(Annotation ann : annotatedElement.getAnnotations()) {
                for (Annotation a : AnnotationMergeCache.INSTANCE.get(ann.annotationType()))
                    propertyInfo.annotations.put(a.annotationType(), a);
                propertyInfo.annotations.put(ann.annotationType(), ann);
            }
        }
    }

    private static void collectNoOverrideAnnotations(PropertyInfo propertyInfo, AnnotatedElement annotatedElement, ClassInfoScanner.ScanBehaviour scanBehaviour) {
        if(annotatedElement != null) {
            for(Annotation ann : annotatedElement.getAnnotations()) {
                if(propertyInfo.hasAnnotation(ann.annotationType())) continue;
                for (Annotation a : AnnotationMergeCache.INSTANCE.get(ann.annotationType()))
                    propertyInfo.annotations.put(a.annotationType(), a);
                propertyInfo.annotations.put(ann.annotationType(), ann);
            }
        }
    }

    private PropertyInfo<?> introspectionGetOrCopyFromParent(String propertyName) {
        PropertyInfo<?> pi = properties.get(propertyName);
        if(pi != null) {
            return pi;
        } else if(parent != null){
            pi = (PropertyInfo<?>) parent.properties.get(propertyName);
            if(pi != null) {
                pi = new PropertyInfo<>(pi, this);
                properties.put(propertyName, pi);
            }
        }
        return pi;
    }

    protected ClassInfo<ClassType> introspect(ClassInfoScanner scanner) {
        for (Method m : type.getDeclaredMethods()) {
            if(!Modifier.isPublic(m.getModifiers()) || Modifier.isStatic(m.getModifiers())) continue;

            String propertyName = getPropertyName(m);
            if (propertyName == null) continue;
            boolean isGetter = isGetter(m);

            PropertyInfo pi = introspectionGetOrCopyFromParent(propertyName);
            if(pi == null) {
                Class propertyType = isGetter ? m.getReturnType() : m.getParameterTypes()[0];
                pi = new PropertyInfo(propertyName, propertyType, this);
                properties.put(propertyName, pi);
            }
            if(isGetter) {
                pi.readMethod = m;
                pi.type = m.getReturnType();
                collectAnnotations(pi, m, scanner.getScanBehaviour());
            }
            else {
                //defer setters processing since they can be multiple with the same name
                pi.writeMethods.put(m.getParameterTypes()[0], m);
            }
        }
        //find public fields
        for(Field f : type.getDeclaredFields()) {
            if(Modifier.isStatic(f.getModifiers()))
                continue;
            PropertyInfo pi = properties.get(f.getName());
            if(pi == null) {
                if(!Modifier.isPublic(f.getModifiers())) continue;
                pi = new PropertyInfo(f.getName(), f.getType(), this);
                pi.field = f;
                collectAnnotations(pi, f, scanner.getScanBehaviour());
                if(scanner.getScanBehaviour().useProperty(pi))
                    properties.put(pi.name, pi);
            } else {
                pi.field = f;
                pi.type = f.getType();
                collectAnnotations(pi, f, scanner.getScanBehaviour());
            }
        }
        postProcessProperties();
        indexAfter();
        return this;
    }

    private void postProcessProperties() {
         for (Map.Entry<String, PropertyInfo> entry : properties.entrySet()) {
             PropertyInfo pi = entry.getValue();
             if(!pi.writeMethods.isEmpty() && pi.writeMethod == null) {
                 if(pi.getField() != null) {
                     pi.writeMethod = (Method) pi.writeMethods.get(pi.getField().getType());
                     collectNoOverrideAnnotations(pi, pi.writeMethod, scanner.getScanBehaviour());
                 } else if(pi.getReadMethod() != null) {
                     pi.writeMethod = (Method) pi.writeMethods.get(pi.getReadMethod().getReturnType());
                     collectNoOverrideAnnotations(pi, pi.writeMethod, scanner.getScanBehaviour());
                 }
             }
             indexProperty(entry.getValue());
         }
        if(parent != null) {
            for (Map.Entry<String, PropertyInfo> entry : (Set<Map.Entry>) parent.properties.entrySet()) {
                if (!properties.containsKey(entry.getKey())) {
                    PropertyInfo pi = entry.getValue();
                    //deep copy property info if it contains generic type to set class info for type variable resolution
                    if(!pi.getType().equals(pi.getGenericType())) {
                        pi = new PropertyInfo(pi, this);
                    }
                    properties.put(entry.getKey(), pi);
                    indexProperty(entry.getValue());
                }
            }
        }
    }

    /**
     * Find property handle with specified selector.
     * Will create whole path of nested objects to get it.
     * @param pathSelector selector to find the property
     * @return property handle
     */
    public PropertyHandle selectProperty(PropertySelector pathSelector) {
        return pathSelector.select(this);
    }

    /**
     * Select property metadata by simple path.
     * For each path element it resolves property info for current class
     * and recursively pass the rest of path to class info of property type
     * @param path property path
     * @return property metadata or null if property not found
     */
    public PropertyInfo getPropertyInfoByPath(final String[] path) {
        LOG.severe("Get property info in " + this.getType().getName() + ": " + Arrays.toString(path));
        if(path == null || path.length == 0) {
            throw new IllegalArgumentException("Path is null or empty");
        }
        String firstPath = path[0];
        PropertyInfo pi = getPropertyInfo(firstPath);
        if (pi != null && pi.getClassInfo() != null) {
            return pi.getClassInfo().getPropertyInfoByPath(Arrays.copyOfRange(path, 1, path.length));
        }
        return pi;
    }

    public <T> PropertyInfo<T> getPropertyInfoByPath(final String [] path, final Class<T> expectedType) {
        PropertyInfo<T> pi = getPropertyInfoByPath(path);
        if(pi == null) return null;
        if(!expectedType.isAssignableFrom(pi.getType())) {
            throw new IllegalArgumentException("Property "+pi.getName()+" is not assignable to class "+expectedType);
        }
        return pi;
    }

    public ClassInfoScanner getScanner() {
        return scanner;
    }
}
