package info.cleanflow.core.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FieldRejectionImplTest {

    private FieldRejectionImpl impl;

    @BeforeEach
    void setUp() {
        impl = new FieldRejectionImpl("mockField", "certainValue", new RuntimeException("mock exception"));
    }

    @Test
    void getField() {
        assertEquals("mockField", impl.getField());
    }

    @Test
    void getException() {
        assertNotNull(impl.getException());
    }

    @Test
    void getMessage() {
        assertEquals("mock exception", impl.getMessage());
    }

    @Test
    void getValueString() {
        assertEquals("certainValue", impl.getValueString());
    }

    @Test
    void optValue() {
        assertEquals("certainValue", impl.optValue(String.class).orElse(null));
    }

}