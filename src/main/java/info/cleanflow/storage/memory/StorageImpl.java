package info.cleanflow.storage.memory;

import info.cleanflow.storage.Storage;

/**
 * The StorageImpl class is an implementation of the Storage interface
 * for mocking and prototyping use cases. It extends the ReadStorageImpl class
 * and provides additional operations for modifying the stored data.
 *
 * @param <K> the type of the keys used in the storage, must implement Comparable
 * @param <T> the type of elements stored in the storage, which must extend the key type
 */
public class StorageImpl<K extends Comparable<K>, T extends K>
        extends ReadStorageImpl<K, T>
        implements Storage<K, T> {

    @Override
    public void deleteByKey(K key) {
        map.remove(key);
    }

    @Override
    public void update(T target) {
        if (map.containsKey(target)) {
            map.put(target, target);
        }
    }

    @Override
    public void insert(T target) {
        map.put(target, target);
    }

}
