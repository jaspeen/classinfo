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
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.*;

/**
 * @author Artem Mironov
 */
public class AnnotationMergeCache extends ClassValue<Set<Annotation>> {
    @Override
    protected Set<Annotation> computeValue(Class<?> type) {
        if (!Annotation.class.isAssignableFrom(type))
            throw new IllegalArgumentException("Only annotation classes can be processed");
        Set<Annotation> res = new HashSet<>();
        for (Annotation a : type.getAnnotations()) {
            if (isApplicableAnnotation(a.annotationType().getAnnotation(Target.class))) {
                res.addAll(get(a.annotationType()));
                res.add(a);
            }
        }
        return res;
    }

    private static boolean isApplicableAnnotation(Target target) {
        if(target == null) return false;
        for(ElementType et : target.value())
            if(et == ElementType.FIELD || et==ElementType.METHOD || et==ElementType.TYPE)
                return true;
        return false;
    }



    public static final AnnotationMergeCache INSTANCE = new AnnotationMergeCache();
}
