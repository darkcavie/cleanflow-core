package info.cleanflow.core.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RejectionCollectorTest {

    private RejectionCollector impl;

    @BeforeEach
    void setUp() {
        impl = new RejectionCollector("mock");
    }

    @Test
    void accept() {
        final var rejectionMock = new RejectionMock();
        assertDoesNotThrow(() -> impl.accept(rejectionMock));
    }

    @Test
    void checkRejections() {
        assertDoesNotThrow(impl::checkRejections);
    }

    @Test
    void checkRejectionsThrows() {
        final var rejectionMock = new RejectionMock();
        impl.accept(rejectionMock);
        assertThrows(InvalidBuiltException.class, impl::checkRejections);
    }

}