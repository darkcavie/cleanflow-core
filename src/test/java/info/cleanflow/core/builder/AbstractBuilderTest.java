package info.cleanflow.core.builder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

class AbstractBuilderTest {

    private BuilderMock mock;

    @BeforeEach
    void setup() {
        mock = new BuilderMock();
    }

    @Test
    void build() {
        final AtomicInteger counter;
        final Source source;

        source = new FineSourceMock();
        assertSame(mock, mock.putSource(source));
        assertSame(mock, mock.putEntitySupplier(EntityMock::new));
        mock.putRejectionConsumer(r -> fail("Must not be rejections"));
        counter = new AtomicInteger();
        mock.build(e -> {
            counter.getAndIncrement();
            assertNotNull(e);
        });
        assertEquals(1, counter.get());
    }

    @Test
    void buildFails() {
        final AtomicInteger counter;
        final Source source;

        source = new FailSourceMock();
        assertSame(mock, mock.putSource(source));
        assertSame(mock, mock.putEntitySupplier(EntityMock::new));
        counter = new AtomicInteger();
        mock.putRejectionConsumer(r -> {
            counter.getAndIncrement();
            assertNotNull(r);
            checkRejection(r);
        });
        mock.build(e -> fail("Must not be a built entity"));
        assertEquals(2, counter.get());
    }

    void checkRejection(Rejection rejection) {
        switch(rejection.getField()) {
            case "name":
                assertEquals("[null value]", rejection.getValueString());
                assertInstanceOf(IllegalArgumentException.class, rejection.getException());
                assertEquals("Name can not be null", rejection.getMessage());
                assertFalse(rejection.optValue(String.class).isPresent());
                break;
            case "start":
                assertEquals("anything", rejection.getValueString());
                assertEquals("anything", rejection.optValue(String.class).orElse(""));
                break;
            default:
                fail("Not an expected field");
        }
    }

    interface Source {
        String getName();

        Optional<String> optStart();

    }

    static class FineSourceMock implements Source {

        public String getName() {
            return "mockName";
        }

        @Override
        public Optional<String> optStart() {
            return Optional.of("2023-01-01T10:00:00");
        }

    }

    static class FailSourceMock implements Source {

        public String getName() {
            return null;
        }

        @Override
        public Optional<String> optStart() {
            return Optional.of("anything");
        }

    }

    static class EntityMock {

        void setName(String name) {
            if(name == null) {
                throw new IllegalArgumentException("Name can not be null");
            }
            assertEquals("mockName", name);
        }

        void setStart(LocalDateTime start) {
            if(start == null) {
                throw new IllegalArgumentException("start can not be null");
            }
            assertEquals(2023, start.getYear());
        }

    }

    static class BuilderMock extends AbstractBuilder<Source, EntityMock> {

        @Override
        protected void assemble(Source source, EntityMock entity) {
            put("name", source.getName(), entity::setName);
            source.optStart().ifPresent(solve("start", entity::setStart, this::toLocalDateTime));
        }

        void toLocalDateTime(String value, Consumer<LocalDateTime> targetConsumer) {
            targetConsumer.accept(LocalDateTime.parse(value));
        }

    }
}
