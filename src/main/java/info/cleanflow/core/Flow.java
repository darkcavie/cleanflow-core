package info.cleanflow.core;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface Flow<S, T> extends BiConsumer<S, Consumer<T>> {

    void flows(S source, Consumer<T> targetConsumer);

    default void accept(S source, Consumer<T> targetConsumer) {
        flows(source, targetConsumer);
    }

}
