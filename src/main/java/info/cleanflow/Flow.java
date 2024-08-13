package info.cleanflow;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The Flow interface represents a flow of data from a source to a target.
 *
 * @param <S> the type of the source data
 * @param <T> the type of the target data
 */
@FunctionalInterface
public interface Flow<S, T> extends BiConsumer<S, Consumer<T>> {

    /**
     * Performs the flow of data from the source to the target consumer.
     *
     * @param source        the source data
     * @param targetConsumer the consumer to handle the target data
     */
    void flows(S source, Consumer<T> targetConsumer);

    /**
     * Performs the flow of data from the source to the target consumer.
     * This method is equivalent to {@link #flows(S, Consumer)}.
     *
     * @param source        the source data
     * @param targetConsumer the consumer to handle the target data
     */
    default void accept(S source, Consumer<T> targetConsumer) {
        flows(source, targetConsumer);
    }

    /**
     * Creates a new consumer that represents the next step in the flow,
     * chaining the current flow with the given next consumer.
     *
     * @param flow         the current flow
     * @param nextConsumer the consumer for the next step in the flow
     * @param <S>          the type of the source data
     * @param <T>          the type of the target data
     * @return a new consumer representing the next step in the flow
     */
    static <S, T> Consumer<S> next(final Flow<S, T> flow, final Consumer<T> nextConsumer) {
        return new Next<>(flow, nextConsumer);
    }

}
