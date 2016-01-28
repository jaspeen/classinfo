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

import java.lang.reflect.*;
import java.util.*;

/**
 * @author Artem Mironov
 */
public class PropertyInfo<PropertyType> extends AnnotatedNode.BaseAnnotatedNode {
    protected Class<PropertyType> type;
    protected Method writeMethod;
    protected Map<Class, Method> writeMethods;
    protected Method readMethod;
    protected Field field;
    protected ClassInfo enclosingClass;

    PropertyInfo(String name, Class<PropertyType> type, ClassInfo enclosingClass) {
        this.enclosingClass = enclosingClass;
        this.name = name;
        this.type = type;
        writeMethods = new HashMap<>();
    }

    PropertyInfo(PropertyInfo<PropertyType> other, ClassInfo newClassInfo) {
        enclosingClass = other.enclosingClass;
        name = other.name;
        type = other.type;
        field = other.field;
        readMethod = other.readMethod;
        writeMethod = other.writeMethod;
        writeMethods = new HashMap<>(other.writeMethods);
    }

    private static final Set<Class> BASIC_TYPES = new HashSet<Class>() {{
        add(int.class);
        add(Integer.class);
        add(long.class);
        add(Long.class);
        add(boolean.class);
        add(Boolean.class);
        add(float.class);
        add(Float.class);
        add(double.class);
        add(Double.class);
        add(String.class);
        add(Object.class);
    }};

    private boolean useNested() {
        return !BASIC_TYPES.contains(type);
    }

    public ClassInfo<?> getClassInfo() {
        return useNested() ? enclosingClass.getScanner().getInfo(type) : null;
    }

    /**
     * Get property value. If value is not readable will thrown exception
     * @param bean instance
     * @return value of the property
     */
    @SuppressWarnings("unchecked")
    public PropertyType getValue(final Object bean) {
        try {
            if (readMethod != null) {
                boolean accessible = readMethod.isAccessible();
                readMethod.setAccessible(true);
                PropertyType res = (PropertyType) readMethod.invoke(bean);
                readMethod.setAccessible(accessible);
                return res;
            }
            else if (field != null && publicOrProtected(field.getModifiers())) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                PropertyType res = (PropertyType) field.get(bean);
                field.setAccessible(accessible);
                return res;
            } else throw new IllegalStateException("Property "+name+" is not readable");

        } catch(IllegalAccessException e) {
            Utils.uncheckedThrow(e);
        } catch(InvocationTargetException e) {
            Utils.uncheckedThrow(e.getTargetException());
        }
        return null;//should not executed
    }

    private void invokeSetter(Method method, Object bean, Object val) {
        try {
            boolean accessible = method.isAccessible();
            method.setAccessible(true);
            method.invoke(bean, val);
            method.setAccessible(accessible);
        } catch (IllegalAccessException e) {
            Utils.uncheckedThrow(e);
        } catch (InvocationTargetException e) {
            Utils.uncheckedThrow(e.getTargetException());
        }
    }

    /**
     * Set property value. If this property is not writable will throw exception.
     * @param bean instance
     * @param val value to set
     */
    public void setValue(Object bean, final PropertyType val) {
        try {
             if (writeMethod != null) {
                 invokeSetter(writeMethod, bean ,val);
            } else if (field != null && publicOrProtected(field.getModifiers())) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(bean, val);
                field.setAccessible(accessible);
            } else throw new IllegalStateException("Property "+name+" is not writable");
        } catch(IllegalAccessException e) {
            Utils.uncheckedThrow(e);
        }
    }

    /**
     * Type of the property
     */
    public Class<PropertyType> getType() {
        return type;
    }

    /**
     * Generic type of the property
     */
    public Type getGenericType() {
        return (field != null ? field.getGenericType() : readMethod != null ? readMethod.getGenericReturnType() : writeMethod.getGenericParameterTypes()[0]);
    }

    /**
     * Returns type info for this property embedding actual property location to resolve type variables
     */
    @SuppressWarnings("unchecked")
    public TypeInfo<PropertyType> getTypeInfo() {
        Type genericType = getGenericType();
        if(genericType == null) return TypeInfo.forClass(type, this);
        else return (TypeInfo<PropertyType>) TypeInfo.forType(genericType, this);
    }

    private static boolean publicOrProtected(int modifiers) {
        return (modifiers & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0;
    }

    /**
     * Check what property is writable
     */
    public boolean isWritable() {
        return writeMethod != null || (field != null && publicOrProtected(field.getModifiers()));
    }

    /**
     * Check what property is readable
     */
    public boolean isReadable() {
        return readMethod != null || (field != null && publicOrProtected(field.getModifiers()));
    }

    public boolean isCollection() {return Collection.class.isAssignableFrom(type);}
    public boolean isMapping() {return Map.class.isAssignableFrom(type);}
    public boolean isIterable() {return Iterable.class.isAssignableFrom(type);}

    /**
     * Returns mutator method for this property if exists
     */
    public Method getWriteMethod() {
        return writeMethod;
    }

    /**
     * Returns accessor method for this property if exists
     */
    public Method getReadMethod() {
        return readMethod;
    }

    /**
     * Returns field associated with this property if exists.
     * Field will be available even when it have private access modifier
     */
    public Field getField() {
        return field;
    }

    /**
     * Write methods what found on class and all parents for this property.
     * They are not directly used but holden for type conversion or other purposes
     */
    public Map<Class, Method> getWriteMethods() {
        return writeMethods;
    }
}
