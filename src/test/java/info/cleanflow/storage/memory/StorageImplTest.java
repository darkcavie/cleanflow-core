package info.cleanflow.storage.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageImplTest {

    private StorageImpl<KeyMock, SourceMock> storage;

    private SourceImplMock source;

    private int received;

    @BeforeEach
    void setUp() {
        storage = new StorageImpl<>();
        source = null;
        received = 0;
    }

    @Test
    void deleteByKey() {
        source = new SourceImplMock("worker", 21);
        assertDoesNotThrow(() -> storage.deleteByKey(source));
    }

    @Test
    void upsertNotPresent() {
        source = new SourceImplMock("student", 12);
        storage.upsert(source);
        storage.findByKey(source, x -> {
            received++;
            assertNotNull(x);
            assertEquals(12, x.getAge());
        });
        assertEquals(1, received);
    }

    @Test
    void upsertWithPresent() {
        source = new SourceImplMock("other", 10);
        storage.insert(source);
        source = new SourceImplMock("other", 13);
        storage.upsert(source);
        storage.findByKey(source, x -> {
            received++;
            assertNotNull(x);
            assertEquals(13, x.getAge());
        });
        assertEquals(1, received);
    }

}