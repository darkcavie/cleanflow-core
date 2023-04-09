package info.cleanflow;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface Flow<S, T> extends BiConsumer<S, Consumer<T>> {

    void flows(S source, Consumer<T> targetConsumer);

    default void accept(S source, Consumer<T> targetConsumer) {
        flows(source, targetConsumer);
    }

    static <S, T> Consumer<S> next(final Flow<S, T> flow,  final Consumer<T> nextConsumer) {
        return new Next<>(flow, nextConsumer);
    }

}
