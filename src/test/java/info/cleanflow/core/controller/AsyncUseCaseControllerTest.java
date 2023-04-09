package info.cleanflow.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class AsyncUseCaseControllerTest {

    private AsyncUseCaseControllerMock impl;

    @BeforeEach
    void setUp() {
        impl = new AsyncUseCaseControllerMock();
    }

    @Test
    void startWithoutResult() {
        final Future<Void> future;

        future = impl.start(this::flowWithoutResult, new Object(), x -> fail("Must not call"));
        assertNotNull(future);
        assertDoesNotThrow(() -> future.get());
    }

    void flowWithoutResult(Object object, Consumer<Object> objectConsumer) {}

    static class AsyncUseCaseControllerMock extends AsyncUseCaseController {

        protected AsyncUseCaseControllerMock() {
            super("mock");
        }

    }

}