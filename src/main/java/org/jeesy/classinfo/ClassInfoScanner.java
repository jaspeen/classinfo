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
import org.jeesy.classinfo.selector.InstantiationBehaviour;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author Artem Mironov
 */
public class ClassInfoScanner extends ClassValue<ClassInfo> {
    private ScanBehaviour scanBehaviour;
    private InstantiationBehaviour instantiationBehaviour;
    private final List<Class<? extends ClassIndex>> scanIndexes;

    public ClassInfoScanner(ScanBehaviour scanBehaviour) {
        this.scanBehaviour = scanBehaviour;
        this.instantiationBehaviour = new DefaultInstantiationBehaviour();
        scanIndexes = Collections.emptyList();
    }

    public ClassInfoScanner(ScanBehaviour scanBehaviour, InstantiationBehaviour instantiationBehaviour) {
        this.scanBehaviour = scanBehaviour;
        this.instantiationBehaviour = instantiationBehaviour;
        scanIndexes = Collections.emptyList();
    }

    public ClassInfoScanner(ScanBehaviour scanBehaviour, InstantiationBehaviour instantiationBehaviour, List<Class<? extends ClassIndex>> scanIndexes) {
        this.scanBehaviour = scanBehaviour;
        this.instantiationBehaviour = instantiationBehaviour;
        this.scanIndexes = scanIndexes;
    }

    public <T> ClassInfo<T> getInfo(final Class<T> type) {
        return get(type);
    }

    public <T> ClassInfo<T> getInfoNoCache(final Class<T> type) {
        Class parentType = type.getSuperclass();
        ClassInfo parentInfo = null;
        if(parentType != null && !parentType.equals(Object.class)) {
            parentInfo = getInfoNoCache(parentType);
        }
        return new ClassInfo<>(type, parentInfo, this).introspect(this);
    }

    public ScanBehaviour getScanBehaviour() {return scanBehaviour;}
    public InstantiationBehaviour getInstantiationBehaviour() {return instantiationBehaviour;}
    public List<Class<? extends ClassIndex>> getScanIndexes() {return scanIndexes;}

    @Override
    protected ClassInfo computeValue(Class<?> type) {
        Class parentType = type.getSuperclass();
        ClassInfo parentInfo = null;
        if(parentType != null && !parentType.equals(Object.class)) {
            parentInfo = get(parentType);
        }
        return new ClassInfo<>(type, parentInfo, this).introspect(this);
    }

    public static class DefaultScanBehaviour implements ScanBehaviour {
        @Override
        public boolean useProperty(final PropertyInfo propertyInfo) {return true;}

        @Override
        public boolean useParent(ClassInfo classInfo) {return true;}

        @Override
        public boolean mergeAnnotations(final Class<? extends Annotation> annotated,
                                        final Class<? extends Annotation> annotation) {return true;}
    }

    public static class DefaultInstantiationBehaviour implements InstantiationBehaviour {
        private static final Map<Class, Class> interfaceToRealizationMapping = new HashMap<Class, Class>() {{
            put(Collection.class, ArrayList.class);
            put(Set.class, HashSet.class);
            put(List.class, ArrayList.class);
            put(Map.class, HashMap.class);
            put(Queue.class, ArrayDeque.class);
            put(Deque.class, ArrayDeque.class);
            put(Deque.class, ArrayDeque.class);
            put(SortedMap.class, TreeMap.class);
            put(SortedSet.class, TreeSet.class);
        }};
        @SuppressWarnings("unchecked")
        @Override
        public <T> T newInstance(final Class<T> type, final AnnotatedNode node) {
            try {
                Class predefinedInstanceClass = interfaceToRealizationMapping.get(type);
                if(predefinedInstanceClass != null)
                    return (T) predefinedInstanceClass.newInstance();
                return type.newInstance();
            } catch (InstantiationException|IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private static final ClassInfoScanner DEFAULT = new ClassInfoScanner(new DefaultScanBehaviour());

    /**
     * Get class info using default scan behaviour and cache
     * @param type class to introspect
     * @param <T> class type
     * @return cached or newly generated class info
     */
    public static <T> ClassInfo<T> classInfo(Class<T> type) {
        return DEFAULT.getInfo(type);
    }

    /**
     * Get class info using default scan behaviour without any caching.
     * @param type class to introspect
     * @param <T> class type
     * @return newly generated class info
     */
    public static <T> ClassInfo<T> classInfoNoCache(Class<T> type) {
        return DEFAULT.getInfoNoCache(type);
    }

    public interface ScanBehaviour {
        /**
         * Check what property is applicable and should be included to properties map.
         *
         * @param propertyInfo property info with all info filled except nested class info
         * @return true if property is applicable
         */
        boolean useProperty(final PropertyInfo propertyInfo);

        /**
         * Decide to use or not property information from parent class.
         *
         * @param classInfo current class info.
         * @return
         */
        boolean useParent(final ClassInfo classInfo);

        /**
         * If true it will merge applicable annotations from specified annotation class to list of property annotations
         *
         * @param annotation
         * @return
         */
        boolean mergeAnnotations(final Class<? extends Annotation> annotated, final Class<? extends Annotation> annotation);
    }
}

