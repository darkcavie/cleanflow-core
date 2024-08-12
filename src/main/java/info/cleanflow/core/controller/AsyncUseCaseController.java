package info.cleanflow.core.controller;

import info.cleanflow.AsyncFlow;
import info.cleanflow.Flow;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static info.cleanflow.Objects.nonNullArgument;
import static info.cleanflow.Objects.nonNullMember;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class AsyncUseCaseController {

    private static final Logger LOG = getLogger(AsyncUseCaseController.class);

    private final ExecutorService executorService;

    private CtrlConfig config;

    protected AsyncUseCaseController(String threadName) {
        nonNullArgument(threadName, "Thread name");
        this.executorService = makeExecutorService(threadName);
        config = new DefaultCtrlConfig();
    }

    protected ExecutorService makeExecutorService(final String threadName) {
        final var threadFactory = new NumberedThreadFactory(threadName);
        return Executors.newCachedThreadPool(threadFactory);
    }

    public void setConfig(CtrlConfig config) {
        this.config = config;
    }

    public void init() {
        nonNullMember(config, "config");
    };

    protected <S, T> Future<Void> start(final Flow<S, T> flow, final S value, final Consumer<T> consumer) {
        final var startFlow = new StartFlow<>(flow, value, consumer);
        return executorService.submit(startFlow, null);
    }

    protected Future<Void> start(Runnable runnable) {
        return executorService.submit(runnable, null);
    }

    protected <S, T> void retry(final Flow<S, T> flow, final S value,  final Consumer<T> consumer) {
        retry(flow, value, consumer, config.getRetryTimes());
    }

    protected <S, T> void retry(final Flow<S, T> flow, final S value,  final Consumer<T> consumer,
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

    protected <S, T> void asyncRetry(final AsyncFlow<S, T> flow, final S value, final Consumer<T> consumer,
             final String errorMessage) {
        asyncRetry(flow, value, consumer, errorMessage, config.getRetryTimes());
    }

    protected <S, T> void asyncRetry(final AsyncFlow<S, T> flow, final S value, final Consumer<T> consumer,
             final String errorMessage, final int remindTries) {
        final Future<Void> future;

        try {
            future = flow.flows(value, consumer);
            manageFuture(future,  errorMessage, RuntimeException.class);
            LOG.info("Success reminded {} tries", remindTries);
        } catch (RuntimeException runtimeException) {
            if(remindTries <= 1) {
                throw runtimeException;
            }
            final var moreTimes = remindTries - 1;
            LOG.info("Try again {} more times", moreTimes);
            asyncRetry(flow, value, consumer, errorMessage, moreTimes);
        }
    }

    protected <X extends RuntimeException> void manageFuture(final Future<Void> future, String message,
            Class<X> exceptionClass) {
        final Throwable cause;

        try {
            if(config.getTimeOut() == 0) {
                future.get();
            } else {
                future.get(config.getTimeOut(), config.getTimeOutUnit());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw buildException(message, e.getMessage(), exceptionClass, null);
        } catch (ExecutionException e) {
            cause = e.getCause();
            if(exceptionClass.isInstance(cause)) {
                throw exceptionClass.cast(cause);
            }
            throw buildException(message, cause.getMessage(), exceptionClass, cause);
        } catch (TimeoutException timeOutEx) {
            throw buildException("Timeout", message, exceptionClass, timeOutEx);
        }
    }

    <X extends RuntimeException> X buildException(final String message, final String subMessage,
              final Class<X> exceptionClass, final Throwable cause) {
        final String formattedMessage;

        formattedMessage = String.format("%s: %s", message, subMessage);
        try {
            if (cause == null) {
                return exceptionClass.getConstructor(String.class)
                        .newInstance(formattedMessage);
            }
            return exceptionClass.getConstructor(String.class, Throwable.class)
                    .newInstance(formattedMessage, cause);
        } catch(ReflectiveOperationException roex) {
            LOG.warn("The class {} does not have a valid constructor, throwing just a RuntimeException",
                    exceptionClass.getName(), roex);
            throw new RuntimeException(formattedMessage, cause);
        }
    }

}
