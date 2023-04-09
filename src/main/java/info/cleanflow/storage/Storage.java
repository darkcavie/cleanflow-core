package info.cleanflow.storage;

public interface Storage<K, T extends K> extends ReadStorage<K, T> {

    void deleteByKey(K key);

    void update(T target);

    void insert(T target);

    default void upsert(T target) {
        if(exist(target)) {
            update(target);
        } else {
            insert(target);
        }
    }

}
