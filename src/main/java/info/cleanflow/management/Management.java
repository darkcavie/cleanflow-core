package info.cleanflow.management;

import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface Management<K, T extends K> {

    Future<Void> get(K key, Consumer<T> consumer);

    Future<Void> getAll(Consumer<T> consumer);

    Future<Void> post(T source, Consumer<T> storedConsumer);

    Future<Void> delete(K key);

}
