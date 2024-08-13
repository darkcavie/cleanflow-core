package info.cleanflow.storage;

/**
 * The Storage interface represents a storage for a specific type of data,
 * which extends the ReadStorage interface for read-only operations.
 *
 * @param <K> the type of the keys used in the storage
 * @param <T> the type of elements stored in the storage, which must extend the key type
 */
public interface Storage<K, T extends K> extends ReadStorage<K, T> {

    /**
     * Deletes an element from the storage based on its key.
     *
     * @param key the key of the element to delete
     */
    void deleteByKey(K key);

    /**
     * Updates an existing element in the storage.
     *
     * @param target the element to update
     */
    void update(T target);

    /**
     * Inserts a new element into the storage.
     *
     * @param target the element to insert
     */
    void insert(T target);

    /**
     * Upserts an element into the storage.
     * If the element already exists in the storage, it is updated.
     * Otherwise, it is inserted as a new element.
     *
     * @param target the element to upsert
     */
    default void upsert(T target) {
        if (exist(target)) {
            update(target);
        } else {
            insert(target);
        }
    }

}
