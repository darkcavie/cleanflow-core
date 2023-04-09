package info.cleanflow.core.controller;

import info.cleanflow.core.Flow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static info.cleanflow.core.Objects.nonNullArgument;

public abstract class AsyncUseCaseController {

    private final ExecutorService executorService;

    protected AsyncUseCaseController(String threadName) {
        nonNullArgument(threadName, "Thread name");
        this.executorService = makeExecutorService(threadName);
    }

    protected ExecutorService makeExecutorService(final String threadName) {
        final var threadFactory = new NumberedThreadFactory(threadName);
        return Executors.newCachedThreadPool(threadFactory);
    }

    public <S, T> Future<Void> start(final Flow<S, T> flow,
                                     final S value,
                                     final Consumer<T> consumer) {
        final var startFlow = new StartFlow<>(flow, value, consumer);
        return executorService.submit(startFlow, null);
    }

}
