package info.cleanflow.core.controller;

import info.cleanflow.Flow;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static info.cleanflow.Objects.nonNullArgument;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class AsyncUseCaseController {

    private static final Logger LOG = getLogger(AsyncUseCaseController.class);

    protected static final int RETRY_TIMES = 3;

    private final ExecutorService executorService;

    protected AsyncUseCaseController(String threadName) {
        nonNullArgument(threadName, "Thread name");
        this.executorService = makeExecutorService(threadName);
    }

    protected ExecutorService makeExecutorService(final String threadName) {
        final var threadFactory = new NumberedThreadFactory(threadName);
        return Executors.newCachedThreadPool(threadFactory);
    }

    public abstract void checkDependencies();

    protected <S, T> Future<Void> start(final Flow<S, T> flow,
                                     final S value,
                                     final Consumer<T> consumer) {
        final var startFlow = new StartFlow<>(flow, value, consumer);
        return executorService.submit(startFlow, null);
    }

    protected Future<Void> start(Runnable runnable) {
        return executorService.submit(runnable, null);
    }

    protected <S, T> void retry(final Flow<S, T> flow,
                                final S value,
                                final Consumer<T> consumer) {
        retry(flow, value, consumer, RETRY_TIMES);
    }

    protected <S, T> void retry(final Flow<S, T> flow,
                                final S value,
                                final Consumer<T> consumer,
                                final int remindTries) {
        try {
            flow.flows(value, consumer);
            LOG.info("Success reminded {} tries", remindTries);
        } catch (RuntimeException runtimeException) {
            if(remindTries <= 1) {
                throw runtimeException;
            }
            final var moreTimes = remindTries - 1;
            LOG.info("Try again {} more times", moreTimes);
            retry(flow, value, consumer, moreTimes);
        }
    }

}
