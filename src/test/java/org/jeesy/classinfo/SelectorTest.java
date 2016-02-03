package org.jeesy.classinfo;

import org.jeesy.classinfo.selector.PropertyHandle;
import org.jeesy.classinfo.selector.PropertySelector;
import org.junit.Test;

/**
 * @author Artem Mironov
 */
public class SelectorTest {
    public static class TestAccessClass {
        public int a;

        public void setA(int a) {
            this.a = a;
        }

        public static class Nested {
            public String d = "abc";
            public Nested() {
                System.out.println("nested constructor");
            }
        }

        public Nested nested;
    }

    @Test
    public void testSelector() {
        TestAccessClass tac = new TestAccessClass();
        PropertySelector selector = PropertySelector.parse("nested").createNullElements();
        PropertyHandle handle = selector.nested("d").resolve(tac);
        System.out.println(handle.getValue());
        handle.setValue("42");
        System.out.println(handle.getValue());
    }
}
