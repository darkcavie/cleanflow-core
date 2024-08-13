package info.cleanflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AsyncFlowTest {

    private AsyncFlowMock mock;

    @BeforeEach
    void setup() {
        mock = new AsyncFlowMock();
    }

    @Test
    void apply() {
        final Future<Void> future;
        final AtomicInteger counter;

        counter = new AtomicInteger();
        future = mock.apply("thing", s -> {
            counter.getAndIncrement();
            assertEquals("thing", s);
        });
        assertDoesNotThrow(() -> future.get());
        assertEquals(1, counter.get());
    }


    static class AsyncFlowMock implements AsyncFlow<Object, String> {

        @Override
        public Future<Void> flows(Object source, Consumer<String> targetConsumer) {
            targetConsumer.accept(source.toString());
            return CompletableFuture.completedFuture(null);
        }

    }

}
