package org.jeesy.classinfo;

import org.junit.Test;

import static org.jeesy.classinfo.ClassInfoScanner.classInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Artem Mironov
 */
public class ClassInfoTypesTest {
    public static class TestBean {
        private String otherField;
        public String getSomeField() {return otherField;}
        public void setSomeField(Integer someField) {this.otherField = someField.toString();}
        public void setSomeField(String someField) {this.otherField = someField;}
        public void setSomeField(Long someField) {this.otherField = someField.toString();}
        public void setSomeField(Double someField) {this.otherField = someField.toString();}
    }

    public static class TestBeanNoGetter {
        private String someField;

        public void setSomeField(Integer someField) {this.someField = someField.toString();}
        public void setSomeField(String someField) {this.someField = someField;}
        public void setSomeField(Long someField) {this.someField = someField.toString();}
        public void setSomeField(Double someField) {this.someField = someField.toString();}

        private String stringField;
        public String getStringField() {return stringField;}
        public void setStringField(Integer stringField) {}
        public void setStringField(Double stringField) {}
    }

    public static class ChildTestBeanNoGetter extends TestBeanNoGetter {
        public void setStringField(String stringField){}

    }

    @Test
    public void testWithGetter() throws Exception {
        ClassInfo<TestBean> ci = classInfo(TestBean.class);
        assertNotNull(ci);
        assertEquals(1, ci.getProperties().size());
        assertEquals(String.class, ci.getPropertyInfo("someField").getType());
        assertEquals(4, ci.getPropertyInfo("someField").getWriteMethods().size());
    }

    @Test
    public void testNoGetter() throws Exception {
        ClassInfo<TestBeanNoGetter> ci = classInfo(TestBeanNoGetter.class);
        assertNotNull(ci);
        assertEquals(2, ci.getProperties().size());
        assertEquals(String.class, ci.getPropertyInfo("someField").getType());
        assertEquals(4, ci.getPropertyInfo("someField").getWriteMethods().size());
    }

    @Test
    public void testNoSetter() throws Exception {
        ClassInfo<TestBeanNoGetter> ci = classInfo(TestBeanNoGetter.class);
        assertNotNull(ci);
        assertEquals(2, ci.getProperties().size());
        assertEquals(String.class, ci.getPropertyInfo("stringField").getType());
        assertNull(ci.getPropertyInfo("stringField").getWriteMethod());
        assertEquals(2, ci.getPropertyInfo("stringField").getWriteMethods().size());
    }

    @Test
    public void testSetterInChild() throws Exception {
        ClassInfo<ChildTestBeanNoGetter> ci = classInfo(ChildTestBeanNoGetter.class);
        assertNotNull(ci);
        assertEquals(2, ci.getProperties().size());
        assertEquals(String.class, ci.getPropertyInfo("stringField").getType());
        assertNotNull(ci.getPropertyInfo("stringField").getWriteMethod());
        assertEquals(3, ci.getPropertyInfo("stringField").getWriteMethods().size());
    }
}
