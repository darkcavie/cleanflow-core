package info.cleanflow.core.builder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class BuilderTest {

    private BuilderMock mock;

    private List<Rejection> rejectionList;

    @BeforeEach
    void setup() {
        mock = new BuilderMock();
        rejectionList = new ArrayList<>();
    }

    @Test
    void putEntitySupplier() {
        assertSame(mock, mock.putEntitySupplier(Object::new));
    }

    @Test
    void putEntitySupplierNullFails() {
        assertThrows(IllegalArgumentException.class, () -> mock.putEntitySupplier(null));
    }

    @Test
    void putRejectionConsumer() {
        assertSame(mock, mock.putRejectionConsumer(rejectionList::add));
    }

    @Test
    void putRejectionConsumerNullIsFine() {
        assertDoesNotThrow(() -> mock.putRejectionConsumer(null));
    }

    @Test
    void putSource() {
        assertSame(mock, mock.putSource(null));
    }

    @Test
    void putPrefix() {
        assertSame(mock, mock.putPrefix("somePrefix"));
    }

    @Test
    void putPrefixNullIsFine() {
        assertDoesNotThrow(() -> mock.putPrefix(null));
    }

    @Test
    void putPrefixBlankIsFine() {
        assertDoesNotThrow(() -> mock.putPrefix("   "));
    }

    @Test
    void build() {
        final AtomicInteger counter;

        counter = new AtomicInteger();
        mock.putSource(new Object())
                .putEntitySupplier(Object::new)
                .putRejectionConsumer(rejectionList::add)
                .build(o -> {
                    counter.getAndIncrement();
                    assertNotNull(o);
                });
        assertEquals(1, counter.get());
        assertEquals(0, rejectionList.size());
    }

    @Test
    void buildWithRejection() {
        final Rejection rejection;

        mock.putSource(new Object())
                .putEntitySupplier(Object::new)
                .putRejectionConsumer(rejectionList::add)
                .putPrefix("prefix");
        mock.reject("field", "value", new IllegalArgumentException("message"));
        mock.build(o -> fail("Must not build because rejection"));
        assertEquals(1, rejectionList.size());
        rejection = rejectionList.get(0);
        assertNotNull(rejection);
        assertEquals("prefix.field", rejection.getField());
        assertEquals("value", rejection.getValueString());
        assertEquals("message", rejection.getMessage());
        assertInstanceOf(IllegalArgumentException.class, rejection.getException());
    }

    @Test
    void transferFails() {
        final Rejection rejection;

        mock.putRejectionConsumer(rejectionList::add);
        mock.transfer("otherField", 1L, l -> {
            throw new IllegalStateException("forcedStateException");
        });
        rejection = rejectionList.get(0);
        assertNotNull(rejection);
        assertEquals("otherField", rejection.getField());
        assertEquals("1", rejection.getValueString());
        assertEquals("forcedStateException", rejection.getMessage());
        assertEquals(1L, rejection.optValue(Long.class).orElse(0L));
        assertInstanceOf(IllegalStateException.class, rejection.getException());
    }

    @Test
    void sendRejectionWithoutConsumer() {
        final Rejection rejection;

        rejection = new RejectionImpl("oneField", LocalDateTime.now(),
                new IllegalStateException("too late"));
        assertDoesNotThrow(() -> mock.sendRejection(rejection));
    }

    @Test
    void transformWithoutSolver() {
        final Consumer<URL> urlConsumer;
        final Consumer<String> stringConsumer;

        urlConsumer = Assertions::assertNull;
        stringConsumer = mock.transform("url", urlConsumer, null);
        assertDoesNotThrow(() -> stringConsumer.accept("anything"));
    }

    @Test
    void transformWithoutValue() {
        final Consumer<URL> urlConsumer;
        final Consumer<String> stringConsumer;

        urlConsumer = Assertions::assertNull;
        stringConsumer = mock.transform("url", urlConsumer, (x, c) -> {});
        assertDoesNotThrow(() -> stringConsumer.accept(null));
    }

    @Test
    void transformFails() {
        final Object value;
        final Consumer<Long> setter;

        value = new Object();
        setter = l -> fail("Must no be called");
        mock.putRejectionConsumer(rejectionList::add)
                .transform("failedField", value, setter, (x, c) -> {
                    throw new IllegalStateException("Transform fails");
                });
        assertEquals(1, rejectionList.size());
    }

    static class BuilderMock extends Builder<Object, Object> {

        @Override
        protected void assemble(Object source, Object entity) {}

    }

}
