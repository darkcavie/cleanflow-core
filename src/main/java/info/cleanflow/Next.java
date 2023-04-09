package info.cleanflow;

import java.util.function.Consumer;

import static info.cleanflow.Objects.nonNullArgument;

class Next<S, T> implements Consumer<S> {

    private final Flow<S, T> flow;

    private final Consumer<T> nextConsumer;

    Next(Flow<S, T> flow, Consumer<T> nextConsumer) {
        this.flow = nonNullArgument(flow, "Flow function");
        this.nextConsumer = nonNullArgument(nextConsumer, "Next consumer");
    }

    @Override
    public void accept(S value) {
        flow.flows(value, nextConsumer);
    }

}
