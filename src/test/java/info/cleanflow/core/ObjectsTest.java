package info.cleanflow.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectsTest {

    @Test
    void nonNullArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Objects.nonNullArgument(null, "argument"));
    }

    @Test
    void nonNullMember() {
        assertThrows(IllegalStateException.class, () -> Objects.nonNullMember(null, "member"));
    }

}