package info.cleanflow.core.builder.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExampleBuilderTest {

    private ExampleBuilder builder;

    @BeforeEach
    void setup() {
        builder = new ExampleBuilder();
    }

    @Test
    void mock() {
        final Consumer<ExampleEntity> targetConsumer;
        final AtomicInteger counter;

        counter = new AtomicInteger();
        targetConsumer = e -> {
            counter.getAndIncrement();
            assertNotNull(e);
        };
        assertDoesNotThrow(() -> builder.putEntitySupplier(ExampleEntity::new)
                .putSource(new ExampleSourceImpl())
                .build(targetConsumer));
        assertEquals(1, counter.get());
    }

}
