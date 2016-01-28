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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static java.util.Arrays.asList;
import static org.jeesy.classinfo.Utils.uncheckedThrow;

/**
 * Method information
 * @author Artem Mironov
 */
public class MethodInfo extends AnnotatedNode.BaseAnnotatedNode {
    private Method method;
    private ParameterInfo [] parameters;

    MethodInfo(Method m) {
        name = m.getName();
        method = m;
        collectParameters(m);
    }

    private void collectParameters(Method m) {

    }

    public Object invoke(Object target, Object ... parameters) {
        try {
            return method.invoke(target,parameters);
        } catch (InvocationTargetException e) {
            uncheckedThrow(e.getTargetException());
        } catch (IllegalAccessException e) {
            uncheckedThrow(e);
        }
        return null;
    }

    public static class ParameterInfo<T> extends AnnotatedNode.BaseAnnotatedNode {
        private Class<T> type;
        private int pos;

        public Class<T> getType(){return type;}
        public int getPostion() {return pos;}
    }

    public Method getMethod() {return method;}
}
