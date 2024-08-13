package info.cleanflow;

import java.util.function.Consumer;

import static info.cleanflow.Objects.nonNullArgument;

/**
 * The Next class is a utility class used to represent the next step in a data flow.
 *
 * @param <S> the type of the source data
 * @param <T> the type of the target data
 */
class Next<S, T> implements Consumer<S> {

    private final Flow<S, T> flow;

    private final Consumer<T> nextConsumer;

    /**
     * Constructs a new Next instance with the given flow and next consumer.
     *
     * @param flow          the flow function representing the current step in the data flow
     * @param nextConsumer  the consumer representing the next step in the data flow
     */
    Next(Flow<S, T> flow, Consumer<T> nextConsumer) {
        this.flow = nonNullArgument(flow, "Flow function");
        this.nextConsumer = nonNullArgument(nextConsumer, "Next consumer");
    }

    @Override
    public void accept(S value) {
        flow.flows(value, nextConsumer);
    }

}
