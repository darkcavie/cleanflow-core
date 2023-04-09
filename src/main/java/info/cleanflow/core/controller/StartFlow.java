package info.cleanflow.core.controller;

import info.cleanflow.Flow;

import java.util.function.Consumer;

import static info.cleanflow.Objects.nonNullArgument;

class StartFlow<S, T> implements Runnable {

    private final Flow<S, T> flow;

    private final S value;

    private final Consumer<T> consumer;

    StartFlow(final Flow<S, T> flow, final S value, final Consumer<T> consumer) {
        this.flow = nonNullArgument(flow, "Initial function");
        this.value = value;
        this.consumer = nonNullArgument(consumer, "Final consumer");
    }

    @Override
    public void run() {
        flow.flows(value, consumer);
    }

}
