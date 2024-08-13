package info.cleanflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlowTest {

    private FlowMock mock;

    @BeforeEach
    void setup() {
        mock = new FlowMock();
    }

    @Test
    void accept() {
        final AtomicInteger counter;

        counter = new AtomicInteger();
        mock.accept("thing", s -> {
            counter.getAndIncrement();
            assertEquals("thing", s);
        });
        assertEquals(1, counter.get());
    }

    @Test
    void next() {
        final Object firstValue;
        final AtomicInteger counter;
        final Consumer<String> finalConsumer;

        firstValue = 1000L;
        counter = new AtomicInteger();
        finalConsumer = s -> {
            counter.getAndIncrement();
            assertEquals("*1000*", s);
        };
        mock.flows(firstValue, Flow.next(this::surround, finalConsumer));
        assertEquals(1, counter.get());
    }

    void surround(String secondValue, Consumer<String> finalConsumer) {
        final String thirdValue;

        thirdValue = String.format("*%s*", secondValue);
        finalConsumer.accept(thirdValue);
    }

    static class FlowMock implements Flow<Object, String> {

        @Override
        public void flows(Object source, Consumer<String> targetConsumer) {
            targetConsumer.accept(source.toString());
        }

    }

}