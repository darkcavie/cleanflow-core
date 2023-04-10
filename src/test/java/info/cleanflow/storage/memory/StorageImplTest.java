package info.cleanflow.storage.memory;

import info.cleanflow.example.sources.PartyKey;
import info.cleanflow.example.sources.PartySource;
import info.cleanflow.example.sources.PartySourceMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageImplTest {

    private StorageImpl<PartyKey, PartySource> storage;

    private PartySourceMock partySourceMock;

    private int received;

    @BeforeEach
    void setUp() {
        storage = new StorageImpl<>();
        partySourceMock = new PartySourceMock();
        received = 0;
    }

    @Test
    void deleteByKey() {
        final var key = partySourceMock.partyKey("worker");
        assertDoesNotThrow(() -> storage.deleteByKey(key));
    }

    @Test
    void upsertNotPresent() {
        final var source = partySourceMock.partySource("student", "2012-01-31", null);
        storage.upsert(source);
        storage.findByKey(source, x -> {
            received++;
            assertNotNull(x);
            assertEquals("2012-01-31", x.getStart());
        });
        assertEquals(1, received);
    }

    @Test
    void upsertWithPresent() {
        var source = partySourceMock.partySource("other", "2000-12-01", null);
        storage.insert(source);
        source = partySourceMock.partySource("other", "2000-12-01", "2023-01-12");
        storage.upsert(source);
        storage.findByKey(source, x -> {
            received++;
            assertNotNull(x);
            assertEquals("2023-01-12", x.optEnd().orElse(null));
        });
        assertEquals(1, received);
    }

}