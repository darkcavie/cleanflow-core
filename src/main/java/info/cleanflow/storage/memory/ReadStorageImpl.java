package info.cleanflow.storage.memory;

import info.cleanflow.storage.ReadStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static info.cleanflow.Objects.nonNullArgument;

/**
 * The ReadStorageImpl class is an implementation of the ReadStorage interface
 * for mocking and prototyping use cases. It provides in-memory storage and retrieval
 * operations for a specific type of data.
 *
 * @param <K> the type of the keys used in the storage, must implement Comparable
 * @param <T> the type of elements stored in the storage, which must extend the key type
 */
public class ReadStorageImpl<K extends Comparable<K>, T extends K>
        implements ReadStorage<K, T> {

    // The internal map used for storing the data.
    protected final Map<K, T> map;

    /**
     * Constructs a new ReadStorageImpl instance with an empty map.
     */
    public ReadStorageImpl() {
        map = new HashMap<>();
    }

    @Override
    public boolean exist(K key) {
        nonNullArgument(key, "Key");
        return map.containsKey(key);
    }

    @Override
    public void findByKey(K key, Consumer<T> consumer) {
        nonNullArgument(key, "Key");
        nonNullArgument(consumer, "Consumer");
        Optional.ofNullable(map.get(key))
                .ifPresent(consumer);
    }

    @Override
    public void findAll(Consumer<T> consumer) {
        nonNullArgument(consumer, "Consumer");
        map.values().forEach(consumer);
    }

}
