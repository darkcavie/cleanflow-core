package info.cleanflow.core;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.BiFunction;

@FunctionalInterface
public interface AsyncFlow<S, T> extends BiFunction<S, Consumer<T>, Future<Void>> {

    Future<Void> flows(S source, Consumer<T> targetConsumer);

    default Future<Void> apply(S source, Consumer<T> targetConsumer) {
        return flows(source, targetConsumer);
    }

}
