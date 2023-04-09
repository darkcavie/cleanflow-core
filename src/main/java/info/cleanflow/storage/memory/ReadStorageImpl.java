package info.cleanflow.storage.memory;

import info.cleanflow.storage.ReadStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static info.cleanflow.Objects.nonNullArgument;

public class ReadStorageImpl<K, T extends K> implements ReadStorage<K, T> {

    protected final Map<K, T> map;

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
