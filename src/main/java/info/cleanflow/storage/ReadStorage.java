package info.cleanflow.storage;

import java.util.function.Consumer;

/**
 * The ReadStorage interface represents a read-only storage for a specific type of data.
 *
 * @param <K> the type of the keys used in the storage
 * @param <T> the type of elements stored in the storage, which must extend the key type
 */
public interface ReadStorage<K, T extends K> {

    /**
     * Checks if a specific key exists in the storage.
     *
     * @param key the key to check for existence
     * @return true if the key exists in the storage, false otherwise
     */
    boolean exist(K key);

    /**
     * Finds and retrieves a specific element by its key.
     * The retrieved element is passed to the provided consumer.
     *
     * @param key      the key of the element to find
     * @param consumer the consumer to handle the retrieved element
     */
    void findByKey(K key, Consumer<T> consumer);

    /**
     * Retrieves all elements from the storage.
     * Each element is passed to the provided consumer.
     *
     * @param consumer the consumer to handle each retrieved element
     */
    void findAll(Consumer<T> consumer);

}
