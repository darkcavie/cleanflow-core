package info.cleanflow.management;

import info.cleanflow.Flow;
import info.cleanflow.core.builder.BuilderSupplier;
import info.cleanflow.core.builder.RejectionCollector;
import info.cleanflow.core.controller.AsyncUseCaseController;
import info.cleanflow.storage.Storage;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import static info.cleanflow.Objects.nonNullArgument;
import static info.cleanflow.Objects.nonNullMember;

public class ManagementImpl<E, K, T extends K>
        extends AsyncUseCaseController
        implements Management<K, T> {

    private final String entityName;

    private Storage<K, T> storage;

    private BuilderSupplier<T, E> builderSupplier;

    private Function<E, T> wrapper;

    protected ManagementImpl(String entityName) {
        super(String.format("%s-manager", entityName));
        this.entityName = entityName;
    }

    public void setStorage(final Storage<K, T> storage) {
        this.storage = storage;
    }

    public void setBuilderSupplier(final BuilderSupplier<T, E> builderSupplier) {
        this.builderSupplier = builderSupplier;
    }

    public void setWrapper(Function<E, T> wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void checkDependencies() {
        nonNullMember(storage, "Storage");
        nonNullMember(builderSupplier, "Entity Builder");
        nonNullMember(wrapper, "Wrapper");
    }

    @Override
    public Future<Void> get(K key, Consumer<T> consumer) {
        nonNullArgument(key, "Key");
        nonNullArgument(consumer, "Consumer");
        return start(storage::findByKey, key, consumer);
    }

    @Override
    public Future<Void> getAll(final Consumer<T> consumer) {
        nonNullArgument(consumer, "Consumer");
        return start(() -> storage.findAll(consumer));
    }

    @Override
    public Future<Void> post(T source, Consumer<T> consumer) {
        nonNullArgument(source, "Source");
        nonNullArgument(consumer, "Consumer");
        return start(this::build, source, consumer);
    }

    protected void build(T source, Consumer<T> consumer) {
        final RejectionCollector collector;

        collector = new RejectionCollector(entityName);
        builderSupplier.get()
                .putSource(source)
                .putRejectionConsumer(collector)
                .build(Flow.next(this::upsert, consumer));
        collector.checkRejections();
    }

    protected void upsert(E entity, Consumer<T> consumer) {
        final T wrapped;

        wrapped = wrapper.apply(entity);
        storage.upsert(wrapped);
        storage.findByKey(wrapped, consumer);
    }

    @Override
    public Future<Void> delete(final K key) {
        nonNullArgument(key, "Key");
        return start(() -> storage.deleteByKey(key));
    }

}
