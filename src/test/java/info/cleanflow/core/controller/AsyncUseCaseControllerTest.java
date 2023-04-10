package info.cleanflow.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class AsyncUseCaseControllerTest {

    private AsyncUseCaseControllerMock impl;

    private int runCounter;

    private int successCounter;

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

    @Test
    void retryOnce() {
        impl.retry(this::flowAlwaysWorks, null, x -> successCounter++);
        assertEquals(1, successCounter);
        assertEquals(1, runCounter);
    }

    void flowAlwaysWorks(Void source, Consumer<Object> consumer) {
        runCounter++;
        consumer.accept(new Object());
    }

    @Test
    void retryMax() {
        final Object value;
        final Consumer<Object> objectConsumer;

        value = new Object();
        objectConsumer = x -> fail("Must not call");
        assertThrows(IllegalStateException.class,
                () -> impl.retry(this::flowAlwaysFails, value, objectConsumer));
        assertEquals(AsyncUseCaseController.RETRY_TIMES, runCounter);
    }

    void flowAlwaysFails(Object source, Consumer<Object> consumer) {
        final var message = String.format("Fail number %d", ++runCounter);
        throw new IllegalStateException(message);
    }

    static class AsyncUseCaseControllerMock extends AsyncUseCaseController {

        protected AsyncUseCaseControllerMock() {
            super("mock");
        }

        @Override
        public void checkDependencies() {}

    }

}