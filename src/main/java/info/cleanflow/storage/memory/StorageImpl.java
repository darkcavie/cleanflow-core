package info.cleanflow.storage.memory;

import info.cleanflow.storage.Storage;

public class StorageImpl<K extends Comparable<K>, T extends K>
        extends ReadStorageImpl<K, T>
        implements Storage<K, T> {

    @Override
    public void deleteByKey(K key) {
        map.remove(key);
    }

    @Override
    public void update(T target) {
        if(map.containsKey(target)) {
            map.put(target, target);
        }
    }

    @Override
    public void insert(T target) {
        map.put(target, target);
    }

}
