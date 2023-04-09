package info.cleanflow.core.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InvalidBuiltExceptionTest {

    private InvalidBuiltException exception;

    @BeforeEach
    void setUp() {
        exception = new InvalidBuiltException("mockEntity", Collections.singleton(new RejectionMock()));
    }

    @Test
    void rejectionStream() {
        assertTrue(exception.rejectionStream().findFirst().isPresent());
    }

    @Test
    void messageStream() {
        assertTrue(exception.messageStream().findFirst().isPresent());
    }

}