package info.cleanflow.storage.memory;

import info.cleanflow.example.sources.PartyKey;
import info.cleanflow.example.sources.PartySource;
import info.cleanflow.example.sources.PartySourceMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

class ReadStorageImplTest {

    private ReadStorageImpl<PartyKey, PartySource> storage;

    private PartySourceMock partySourceMock;

    @BeforeEach
    void setUp() {
        storage = new ReadStorageImpl<>();
        partySourceMock = new PartySourceMock();
    }

    @Test
    void exist() {
        final var source = partySourceMock.partyKey("master");
        assertFalse(storage.exist(source));
    }

    @Test
    void findByKey() {
        final Consumer<PartySource> consumer;
        final PartyKey keyMock;

        consumer = x -> fail("Found Nothing");
        keyMock = partySourceMock.partyKey("master");
        assertDoesNotThrow(() -> storage.findByKey(keyMock, consumer));
    }

    @Test
    void findAll() {
        final Consumer<PartySource> consumer;

        consumer = x -> fail("Found Nothing");
        assertDoesNotThrow(() -> storage.findAll(consumer));
    }

}