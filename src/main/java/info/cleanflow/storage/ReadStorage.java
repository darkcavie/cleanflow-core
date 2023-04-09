package info.cleanflow.storage;

import java.util.function.Consumer;

public interface ReadStorage<K, T extends K> {

    boolean exist(K key);

    void findByKey(K key, Consumer<T> consumer);

    void findAll(Consumer<T> consumer);

}
