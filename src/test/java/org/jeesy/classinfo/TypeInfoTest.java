package org.jeesy.classinfo;

import org.junit.Test;
import org.jeesy.classinfo.converter.api.Converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.jeesy.classinfo.ClassInfoScanner.classInfo;

/**
 * @author Artem Mironov
 */
public class TypeInfoTest {
    public static class TypeTestClass<B extends Map<String, Integer>> {
        public <T extends B> Map<Set<Converter<String, T>>, List<Map<String, Integer>>> [] getSomeField() {
            return new Map[0];
        }

        public String[] simpleArrayMethod() {
            return new String[] {"123"};
        }

        public <SomeType> void simpleGenericMethod(SomeType g) {
        }

        private String stringField;

        public String getStringField() {
            System.out.println("GetStringField");
            return stringField;
        }

    }

    public static class ChildTypeTestClass<C> extends TypeTestClass<HashMap<String, Integer>> {
        //@Override
        public void simpleGenericMethod(Integer g) {
            super.simpleGenericMethod(g);
        }
        @Override
        public String getStringField() {
            System.out.println("GetStringField");
            return null;
        }
    }

    @Test
    public void testInfo() throws Exception {
        PropertyInfo pi = classInfo(ChildTypeTestClass.class).getPropertyInfo("someField");
        TypeInfo ti = pi.getTypeInfo();
        assertTrue(ti.isArray());
        assertEquals(Set.class, ti.getComponentType().resolveParameter(0).getRawType());
    }

}
