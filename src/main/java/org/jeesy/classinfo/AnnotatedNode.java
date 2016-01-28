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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface for elements with name and annotations
 * @author Artem Mironov
 */
public interface AnnotatedNode {
    /**
     * Node name
     */
    String getName();

    /**
     * Annotation instance linked to this node
     * @param annotationClass class of annotation to lookup
     * @param <T> annotation generic type
     * @return annotation instance or null if nothing found
     */
    <T extends Annotation> T getAnnotation(final Class<T> annotationClass);

    /**
     * Check what annotation present
     * @param annotationClass class of annotation to check
     * @return true if annotation present
     */
    boolean hasAnnotation(final Class<? extends Annotation> annotationClass);

    /**
     * Get list of annotations linked to this node
     * @return empty collection if no annotations
     */
    Collection<Annotation> getAnnotataions();

    /**
     * Simple implementation of {@link AnnotatedNode} what stores all in fields
     */
    class BaseAnnotatedNode implements AnnotatedNode {
        protected String name;
        protected Map<Class<? extends Annotation>, Annotation> annotations;

        @Override
        public String getName() { return name; }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
            return (T) annotations.get(annotationClass);
        }

        @Override
        public boolean hasAnnotation(final Class<? extends Annotation> annotationClass) {
            return annotations.containsKey(annotationClass);
        }

        @Override
        public Collection<Annotation> getAnnotataions() {
            return Collections.unmodifiableCollection(annotations.values());
        }

        protected BaseAnnotatedNode() { annotations= new HashMap<>();}
        public BaseAnnotatedNode(String name, Map<Class<? extends Annotation>, Annotation> annotations) {
            this.name = name;
            this.annotations = annotations;
        }

        public BaseAnnotatedNode(String name) {
            this.name = name;
            this.annotations = Collections.emptyMap();
        }
    }
}
