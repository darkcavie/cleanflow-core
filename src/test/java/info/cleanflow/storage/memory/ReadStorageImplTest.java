package info.cleanflow.storage.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

class ReadStorageImplTest {

    private ReadStorageImpl<KeyMock, SourceMock> storage;

    @BeforeEach
    void setUp() {
        storage = new ReadStorageImpl<>();
    }

    @Test
    void exist() {
        final var source = new KeyImplMock("master");
        assertFalse(storage.exist(source));
    }

    @Test
    void findByKey() {
        final Consumer<SourceMock> consumer;
        final KeyMock keyMock;

        consumer = x -> fail("Found Nothing");
        keyMock = new KeyImplMock("master");
        assertDoesNotThrow(() -> storage.findByKey(keyMock, consumer));
    }

    @Test
    void findAll() {
        final Consumer<SourceMock> consumer;

        consumer = x -> fail("Found Nothing");
        assertDoesNotThrow(() -> storage.findAll(consumer));
    }

}