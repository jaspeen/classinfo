package org.jeesy.classinfo;

import org.jeesy.classinfo.converter.DefaultConverter;
import org.junit.Test;

import java.util.*;

import static java.util.Arrays.asList;
import static org.jeesy.classinfo.converter.DefaultConverter.defaultConverter;
import static org.junit.Assert.*;

/**
 * @author Artem Mironov
 */
public class ConvertersTest {

    private static <T> void assertFromString(T obj, String val) {
        DefaultConverter dc = defaultConverter();
        T test =  dc.fromString(TypeInfo.forClass((Class < T >) obj.getClass()), val);
        assertEquals(obj, test);
    }

    private static void assertOrderedCollectionEquals(Collection expected, Collection actual) {
        if(expected ==null || actual == null) fail("Expecter or actual collection is null");
        assertEquals("Expected collection size "+expected.size()+" != actual collection size "+actual.size(), expected.size(), actual.size());
        Iterator expectedIterator = expected.iterator();
        Iterator actualIterator = actual.iterator();
        while(expectedIterator.hasNext() && actualIterator.hasNext()) {
            assertEquals(expectedIterator.next(), actualIterator.next());
        }
    }

    private static <T extends Collection> void assertCollectionFromString(TypeInfo<T> collectionType, T obj, String val) {
        T test = defaultConverter().fromString(collectionType, val);
        assertTrue(collectionType.getRawType().isAssignableFrom(test.getClass()));
        assertOrderedCollectionEquals(obj, test);
    }

    private static class TestClass {
        private String val;
        public TestClass(String val) {this.val = val;}
        @Override
        public boolean equals(Object other) {return ((TestClass)other).val.equals(val);}

    }

    @Test
    public void testDefaultConverter() throws Exception {
        assertFromString("42","42");
        assertFromString(42, "42");
        assertFromString(Integer.valueOf(42), "42");
        assertFromString(Integer.MAX_VALUE, String.valueOf(Integer.MAX_VALUE));
        assertFromString(42L, "42");
        assertFromString(Long.valueOf(42L), "42");
        assertFromString(Long.MAX_VALUE, String.valueOf(Long.MAX_VALUE));
        assertFromString((short)42, "42");
        assertFromString(Short.valueOf((short)42), "42");
        assertFromString(Short.MAX_VALUE, String.valueOf(Short.MAX_VALUE));
        assertFromString((byte)42, "42");
        assertFromString(Byte.valueOf((byte)42), "42");
        assertFromString(Byte.MAX_VALUE, String.valueOf(Byte.MAX_VALUE));
        assertFromString(false, "42");
        assertFromString(false, "someunknownvalue");
        assertFromString(true, "true");
        assertFromString(true, "TruE");
        assertFromString(Boolean.FALSE, "42");
        assertFromString(Boolean.TRUE, "tRUE");
        assertFromString(new TestClass("somestringvalue"), "somestringvalue");
    }

    @Test
    public void testDefaultConverterSimple() {
        Integer res = defaultConverter().convert("42", Integer.class);
        assertEquals((Integer)42, res);
        int resInt = defaultConverter().convert("42", int.class);
        assertEquals(42, resInt);
        long resLong = defaultConverter().convert("", long.class);
        assertEquals(0, resLong);
    }

    @Test
    public void testDefaultConverterCollections() throws Exception {
        assertCollectionFromString(new TypeInfo<Set<String>>(){}, new HashSet<>(asList("first", "second","third")), "first,second,third");
    }

    @Test
    public void testConverterWithConstructorConverter() throws Exception {
        assertCollectionFromString(new TypeInfo<List<TestClass>>(){}, asList(new TestClass("first"), new TestClass("second"), new TestClass("third")), "first,second,third");
    }

    @Test
    public void testDefaultConverterCollectionsWithComponent() throws Exception {
        assertCollectionFromString(new TypeInfo<Set<Integer>>(){}, new HashSet<>(asList(1, 166,99)), "1,166,99");
    }

}
