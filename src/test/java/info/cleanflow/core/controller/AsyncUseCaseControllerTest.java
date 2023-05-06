package info.cleanflow.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void setTimeOut() {
        assertDoesNotThrow(() -> impl.setTimeOut(0));
    }

    @Test
    void setTimeOutNegativeFails() {
        assertThrows(IllegalArgumentException.class, () -> impl.setTimeOut(-1));
    }

    @Test
    void setTimeOutUnitNullFails() {
        assertThrows(IllegalArgumentException.class, () -> impl.setTimeOutUnit(null));
    }

    @Test
    void setTimeOutUnit() {
        assertDoesNotThrow(() -> impl.setTimeOutUnit(TimeUnit.MINUTES));
    }

    @Test
    void asyncRetry() {
        impl.asyncRetry(this::asyncAlwaysWork, "x", c -> {
            successCounter++;
            assertNotNull(c);
        }, "Not used error");
        assertEquals(1, successCounter);
        assertEquals(1, runCounter);
    }

    Future<Void> asyncAlwaysWork(String request, Consumer<Object> responseConsumer) {
        runCounter++;
        assertEquals("x", request);
        responseConsumer.accept(new Object());
        return CompletableFuture.completedFuture(null);
    }

    @Test
    void asyncRetryFails() {
        assertThrows(RuntimeException.class,
                () -> impl.asyncRetry(this::asyncAlwaysFails, "x", c -> successCounter++, "async"));
        assertEquals(0, successCounter);
        assertEquals(3, runCounter);
    }

    Future<Void> asyncAlwaysFails(String request, Consumer<Object> responseConsumer) {
        final CompletableFuture<Void> future;

        runCounter++;
        future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("It never works"));
        return future;
    }

    static class AsyncUseCaseControllerMock extends AsyncUseCaseController {

        protected AsyncUseCaseControllerMock() {
            super("mock");
        }

        @Override
        public void checkDependencies() {}

    }

}