package info.cleanflow.management;

import info.cleanflow.core.builder.Builder;
import info.cleanflow.example.builder.PartyBuilder;
import info.cleanflow.example.entity.Party;
import info.cleanflow.example.sources.PartyKey;
import info.cleanflow.example.sources.PartySource;
import info.cleanflow.example.sources.PartySourceMock;
import info.cleanflow.example.wrapper.PartyWrapper;
import info.cleanflow.storage.memory.StorageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class ManagementImplTest {

    private ManagementImpl<Party, PartyKey, PartySource> impl;

    private PartySourceMock partySourceMock;

    private int received;

    @BeforeEach
    void setUp() {
        impl = new ManagementImpl<>("party");
        impl.setStorage(new StorageImpl<>());
        impl.setBuilderSupplier(this::builder);
        impl.setWrapper(PartyWrapper::new);
        partySourceMock = new PartySourceMock();
        received = 0;
    }

    Builder<PartySource, Party> builder() {
        return new PartyBuilder()
                .putEntitySupplier(Party::new);
    }

    @Test
    void checkDependencies() {
        assertDoesNotThrow(impl::checkDependencies);
    }

    @Test
    void get() {
        final Consumer<PartySource> consumer;
        final PartyKey key;
        final Future<Void> future;

        key = partySourceMock.partyKey("no-one");
        consumer = x -> fail("Must not call");
        future = impl.get(key, consumer);
        assertDoesNotThrow(() -> future.get());
    }

    @Test
    void getAll() {
        final Consumer<PartySource> consumer;
        final Future<Void> future;

        consumer = x -> fail("Must not call");
        future = impl.getAll(consumer);
        assertDoesNotThrow(() -> future.get());
    }

    @Test
    void post() {
        final Consumer<PartySource> consumer;
        final PartySource original;
        final Future<Void> future;

        original = partySourceMock.partySource("someone", "2001-01-01", null);
        consumer = x -> {
            received++;
            assertNotNull(x);
            assertEquals("someone", x.getName());
            assertEquals("2001-01-01", x.getStart());
            assertFalse(x.optEnd().isPresent());
        };
        future = impl.post(original, consumer);
        assertDoesNotThrow(() -> future.get());
        assertEquals(1, received);
    }

    @Test
    void delete() {
        final var key = partySourceMock.partyKey("anything");
        final var future = impl.delete(key);
        assertDoesNotThrow(() -> future.get());
    }

}
