package com.example.utils;

import lombok.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommonUtilsTest {

    @Test
    void contains_test() {
        List<MockClass> objects = new ArrayList<>();
        
        MockClass o1 = new MockClass();
        o1.setIntField(1);
        o1.setStringField(new String("s1"));
        o1.setListField(Arrays.asList(
                new String("e11"),
                new String("e12")));
        o1.setClassField(new MockClass.InnerMockClass(
                new String("is1"),
                EnumClass.VAL1
        ));
        objects.add(o1);

        MockClass o2 = new MockClass();
        o2.setIntField(2);
        o2.setStringField(null);
        o2.setListField(Arrays.asList(
                new String("e21"),
                new String("e22")));
        o2.setClassField(new MockClass.InnerMockClass(
                new String("is2"),
                EnumClass.VAL2
        ));
        objects.add(o2);


        MockClass searchedObj1 = new MockClass();
        searchedObj1.setIntField(1);
        searchedObj1.setStringField(new String("s1"));
        searchedObj1.setListField(Arrays.asList(
                new String("e11"),
                new String("e12")));
        searchedObj1.setClassField(new MockClass.InnerMockClass(
                new String("is2"),
                EnumClass.VAL2
        ));

        MockClass searchedObj2 = new MockClass();
        searchedObj2.setIntField(2);
        searchedObj2.setStringField(null);
        searchedObj2.setListField(Arrays.asList(
                new String("e21"),
                new String("e22")));
        searchedObj2.setClassField(new MockClass.InnerMockClass(
                new String("is1"),
                EnumClass.VAL1
        ));


        assertTrue(CommonUtils.contains(objects, searchedObj1, MockClass::getIntField, MockClass::getStringField, MockClass::getListField));
        assertFalse(CommonUtils.contains(objects, searchedObj1, MockClass::getIntField, MockClass::getClassField));

        assertTrue(CommonUtils.contains(objects, searchedObj2, MockClass::getIntField, MockClass::getStringField, MockClass::getListField));
        assertFalse(CommonUtils.contains(objects, searchedObj2, MockClass::getIntField, MockClass::getClassField));
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class MockClass {
        private int intField;
        private String stringField;
        private List<String> listField;
        private Set<String> setField;
        private InnerMockClass classField;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        static class InnerMockClass {
            private String stringField;
            private EnumClass enumField;

            @Override
            public boolean equals(Object o) {
                if (o == null)
                    return false;

                if (this == o)
                    return true;

                if (o instanceof InnerMockClass other)
                    return this.stringField.equals(other.stringField)
                            && this.enumField.equals(other.getEnumField());

                return false;
            }
        }
    }

    enum EnumClass {
        VAL1, VAL2
    }
}