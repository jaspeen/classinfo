package org.jeesy.classinfo;

import org.jeesy.classinfo.converter.impl.BeanConverter;
import org.junit.Test;

import static org.jeesy.classinfo.TypeInfo.forClass;
import static org.jeesy.classinfo.converter.DefaultConverter.defaultConverter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Artem Mironov
 */
public class BeanConverterTest {
    public static class BeanA {
        private String field1;
        private String field2;
        private Integer field3;

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public Integer getField3() {
            return field3;
        }

        public void setField3(Integer field3) {
            this.field3 = field3;
        }
    }

    public static class BeanB {
        private String fieldUnknown;
        private Integer field2;
        private String field3;

        public String getFieldUnknown() {
            return fieldUnknown;
        }

        public void setFieldUnknown(String fieldUnknown) {
            this.fieldUnknown = fieldUnknown;
        }

        public Integer getField2() {
            return field2;
        }

        public void setField2(Integer field2) {
            this.field2 = field2;
        }

        public String getField3() {
            return field3;
        }

        public void setField3(String field3) {
            this.field3 = field3;
        }
    }

    @Test
    public void testBeanConversion() {
        BeanA beanA = new BeanA();
        beanA.setField1("field1Value");
        beanA.setField2("146");
        beanA.setField3(42);
        BeanB beanB = (BeanB) defaultConverter().converterByType(BeanConverter.class).convert(beanA, (TypeInfo)forClass(BeanA.class), (TypeInfo)forClass(BeanB.class));
        assertNotNull(beanB);
        assertEquals(null, beanB.getFieldUnknown());
        assertEquals((Integer)146, beanB.getField2());
        assertEquals("42", beanB.getField3());
    }
}
