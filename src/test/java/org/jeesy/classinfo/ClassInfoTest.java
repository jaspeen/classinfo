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

import org.jeesy.classinfo.annotations.PropertyAnnotation;
import org.junit.Before;
import org.junit.Test;

import static org.jeesy.classinfo.ClassInfoScanner.classInfo;
import static org.junit.Assert.*;

/**
 * @author Artem Mironov
 */
public class ClassInfoTest {

    public static class ParentBean {
        @PropertyAnnotation(requiredValue = 1)
        private String stringPropWithSetter = "stringPropWithSetterValue";

        @PropertyAnnotation(requiredValue = 2)
        private String stringPropWithGetterAndSetter = "stringPropWithGetterAndSetterValue";

        @PropertyAnnotation(requiredValue = 3)
        private String stringPropWithGetter = "stringPropWithGetterValue";

        private String stringPropWithAnnotatedGetter = "stringPropWithAnnotatedGetterValue";
        private String stringPropWithAnnotatedSetter = "stringPropWithAnnotatedSetterValue";
        private String stringPropWithAnnotatedGetterAndBothMethods = "stringPropWithAnnotatedGetterAndBothMethodsValue";
        private String stringPropWithGetterAndSetterNoAnnotations = "stringPropWithGetterAndSetterNoAnnotationsValue";

        @PropertyAnnotation(requiredValue = 8)
        public String stringPropPublicNoSetter = "stringPropPublicNoSetterValue";
        @PropertyAnnotation(requiredValue = 9)
        public String stringPropPublicNoGetter = "stringPropPublicNoGetterValue";
        @PropertyAnnotation(requiredValue = 10)
        public String stringPropOnlyField = "stringPropOnlyFieldValue";

        @PropertyAnnotation(requiredValue = -2, value = "field")
        private String stringPropAnnotationPriority = "stringPropAnnotationPriorityValue";

        @PropertyAnnotation(requiredValue = 4)
        public String getStringPropWithAnnotatedGetter() {
            return "get"+stringPropWithAnnotatedGetter;
        }

        @PropertyAnnotation(requiredValue = 5)
        public void setStringPropWithAnnotatedSetter(String stringPropWithAnnotatedSetter) {
            this.stringPropWithAnnotatedSetter = "set"+stringPropWithAnnotatedSetter;
        }

        @PropertyAnnotation(requiredValue = 6)
        public String getStringPropWithAnnotatedGetterAndBothMethods() {
            return "get"+stringPropWithAnnotatedGetterAndBothMethods;
        }

        @PropertyAnnotation(requiredValue = -2, value = "getter")
        public String getStringPropAnnotationPriority() {
            return stringPropAnnotationPriority;
        }

        @PropertyAnnotation(requiredValue = -2, value = "setter")
        public void setStringPropAnnotationPriority(String stringPropAnnotationPriority) {
            this.stringPropAnnotationPriority = stringPropAnnotationPriority;
        }

        public void setStringPropWithAnnotatedGetterAndBothMethods(String stringPropWithAnnotatedGetterAndBothMethods) {
            this.stringPropWithAnnotatedGetterAndBothMethods = "set"+stringPropWithAnnotatedGetterAndBothMethods;
        }

        public void setStringPropWithSetter(String arg) {
            stringPropWithSetter = "set"+arg;
        }

        public String getStringPropWithGetter() {
            return "get"+stringPropWithGetter;
        }

        public String getStringPropWithGetterAndSetter() {
            return "get"+stringPropWithGetterAndSetter;
        }

        public void setStringPropWithGetterAndSetter(String stringPropWithGetterAndSetter) {
            this.stringPropWithGetterAndSetter = "set"+stringPropWithGetterAndSetter;
        }

        public String getStringPropWithGetterAndSetterNoAnnotations() {
            return "get"+stringPropWithGetterAndSetterNoAnnotations;
        }

        public void setStringPropWithGetterAndSetterNoAnnotations(String stringPropWithGetterAndSetterNoAnnotations) {
            this.stringPropWithGetterAndSetterNoAnnotations = "set"+stringPropWithGetterAndSetterNoAnnotations;
        }


        public String getStringPropertyWithoutField() {
            return "stringPropertyWithoutFieldValue";
        }

        @PropertyAnnotation(requiredValue = 7, value = "getter")
        public String getAnnotatedStringPropertyWithoutField() {
            return "annotatedStringPropertyWithoutFieldValue";
        }

        //@PropertyAnnotation(requiredValue = 7, value = "setter")
        public void setAnnotatedStringPropertyWithoutField(String someProp) {
            //do nothing
        }

        @PropertyAnnotation(requiredValue = 8, value = "getter")
        public String getStringPropPublicNoSetter() {
            return "get"+stringPropPublicNoSetter;
        }

        @PropertyAnnotation(requiredValue = 9, value = "setter")
        public void setStringPropPublicNoGetter(String stringPropPublicNoGetter) {
            this.stringPropPublicNoGetter = "set"+stringPropPublicNoGetter;
        }
    }

    private ClassInfo<ParentBean> info;
    private Object holder;

    @Before
    public void setUp() {
        info = classInfo(ParentBean.class);
        ParentBean proxy = new ParentBean();
        holder = proxy;
    }

    @Test
    public void testScan() {
        assertEquals(ParentBean.class, info.getType());
        assertNull(info.getParent());
        assertEquals(13, info.getProperties().size());
    }

    @Test
    public void testStringPropWithSetter() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropWithSetter");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropWithSetter", pi.getName());
        assertNull(pi.getReadMethod());
        assertNotNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertTrue(pi.isWritable());
        assertFalse(pi.isReadable());
        assertEquals(1, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        try {
            pi.getValue(holder);
            fail("Exception wasn't thrown for writeonly property");
        } catch(RuntimeException e) {
            //pass
        }
    }


    @Test
    public void testStringPropWithGetterAndSetter() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropWithGetterAndSetter");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropWithGetterAndSetter", pi.getName());
        assertNotNull(pi.getReadMethod());
        assertNotNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(2, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        assertTrue(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("getstringPropWithGetterAndSetterValue", pi.getValue(holder));
        pi.setValue(holder, "testvalue");
        assertEquals("getsettestvalue", pi.getValue(holder));
    }

    @Test
    public void testStringPropWithGetter() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropWithGetter");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropWithGetter", pi.getName());
        assertNotNull(pi.getReadMethod());
        assertNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(3, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        assertFalse(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("getstringPropWithGetterValue", pi.getValue(holder));
        try {
            pi.setValue(holder, "testvalue");
            fail("Exception wasn't thrown for readonly property");
        } catch(RuntimeException e) {
            //pass
        }

    }

    @Test
    public void testStringPropWithAnnotatedGetter() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropWithAnnotatedGetter");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropWithAnnotatedGetter", pi.getName());
        assertNotNull(pi.getReadMethod());
        assertNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(4, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        assertFalse(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("getstringPropWithAnnotatedGetterValue", pi.getValue(holder));
        try {
            pi.setValue(holder, "testvalue");
            fail("Exception wasn't thrown for readonly property");
        } catch(RuntimeException e) {
            //pass
        }
    }

    @Test
    public void testStringPropWithAnnotatedSetter() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropWithAnnotatedSetter");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropWithAnnotatedSetter", pi.getName());
        assertNull(pi.getReadMethod());
        assertNotNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(5, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        assertTrue(pi.isWritable());
        assertFalse(pi.isReadable());
        pi.setValue(holder, "testvalue");
        try {
            pi.getValue(holder);
            fail("Exception wasn't thrown for readonly property");
        } catch(RuntimeException e) {
            //pass
        }
    }

    @Test
    public void testStringPropWithAnnotatedGetterAndBothMethods() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropWithAnnotatedGetterAndBothMethods");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropWithAnnotatedGetterAndBothMethods", pi.getName());
        assertNotNull(pi.getReadMethod());
        assertNotNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(6, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        assertTrue(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("getstringPropWithAnnotatedGetterAndBothMethodsValue", pi.getValue(holder));
        pi.setValue(holder, "testvalue");
        assertEquals("getsettestvalue", pi.getValue(holder));
    }

    @Test
    public void testStringPropWithGetterAndSetterNoAnnotations() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropWithGetterAndSetterNoAnnotations");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropWithGetterAndSetterNoAnnotations", pi.getName());
        assertNotNull(pi.getReadMethod());
        assertNotNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(0, pi.getAnnotataions().size());
        assertTrue(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("getstringPropWithGetterAndSetterNoAnnotationsValue", pi.getValue(holder));
        pi.setValue(holder, "testvalue");
        assertEquals("getsettestvalue", pi.getValue(holder));
    }

    @Test
    public void testStringPropertyWithoutField() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropertyWithoutField");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropertyWithoutField", pi.getName());
        assertNotNull(pi.getReadMethod());
        assertNull(pi.getWriteMethod());
        assertNull(pi.getField());
        assertEquals(0, pi.getAnnotataions().size());
        assertFalse(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("stringPropertyWithoutFieldValue", pi.getValue(holder));
        try {
            pi.setValue(holder, "testvalue");
            fail("Exception wasn't thrown for readonly property");
        } catch(RuntimeException e) {
            //pass
        }

    }

    @Test
    public void testAnnotatedStringPropertyWithoutField() {
        PropertyInfo<String> pi = info.getPropertyInfo("annotatedStringPropertyWithoutField");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("annotatedStringPropertyWithoutField", pi.getName());
        assertNotNull(pi.getReadMethod());
        assertNotNull(pi.getWriteMethod());
        assertNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(7, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        assertEquals("getter", pi.getAnnotation(PropertyAnnotation.class).value());
        assertTrue(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("annotatedStringPropertyWithoutFieldValue", pi.getValue(holder));
        pi.setValue(holder, "testvalue");
        assertEquals("annotatedStringPropertyWithoutFieldValue", pi.getValue(holder));
    }

    @Test
    public void testStringPropAnnotationPriority() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropAnnotationPriority");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropAnnotationPriority", pi.getName());
        assertNotNull(pi.getReadMethod());
        assertNotNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(-2, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        //field annotations always wins if in this class
        assertEquals("field", pi.getAnnotation(PropertyAnnotation.class).value());
    }

    @Test
    public void testStringPropPublicNoSetter() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropPublicNoSetter");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropPublicNoSetter", pi.getName());
        assertNotNull(pi.getReadMethod());
        assertNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(8, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        assertEquals("", pi.getAnnotation(PropertyAnnotation.class).value());
        assertTrue(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("getstringPropPublicNoSetterValue", pi.getValue(holder));
        pi.setValue(holder, "testvalue");
        assertEquals("gettestvalue", pi.getValue(holder));
    }

    @Test
    public void testStringPropPublicNoGetter() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropPublicNoGetter");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropPublicNoGetter", pi.getName());
        assertNull(pi.getReadMethod());
        assertNotNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(9, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        assertEquals("", pi.getAnnotation(PropertyAnnotation.class).value());
        assertTrue(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("stringPropPublicNoGetterValue", pi.getValue(holder));
        pi.setValue(holder, "testvalue");
        assertEquals("settestvalue", pi.getValue(holder));
    }

    @Test
    public void testStringPropOnlyField() {
        PropertyInfo<String> pi = info.getPropertyInfo("stringPropOnlyField");
        assertNotNull(pi);
        assertEquals(String.class, pi.getType());
        assertNull(pi.getClassInfo());
        assertEquals("stringPropOnlyField", pi.getName());
        assertNull(pi.getReadMethod());
        assertNull(pi.getWriteMethod());
        assertNotNull(pi.getField());
        assertEquals(1, pi.getAnnotataions().size());
        assertEquals(10, pi.getAnnotation(PropertyAnnotation.class).requiredValue());
        assertEquals("", pi.getAnnotation(PropertyAnnotation.class).value());
        assertTrue(pi.isWritable());
        assertTrue(pi.isReadable());
        assertEquals("stringPropOnlyFieldValue", pi.getValue(holder));
        pi.setValue(holder, "testvalue");
        assertEquals("testvalue", pi.getValue(holder));
    }
}
