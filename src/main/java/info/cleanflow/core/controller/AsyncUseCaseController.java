package info.cleanflow.core.controller;

import info.cleanflow.AsyncFlow;
import info.cleanflow.Flow;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static info.cleanflow.Objects.nonNullArgument;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class AsyncUseCaseController {

    private static final Logger LOG = getLogger(AsyncUseCaseController.class);

    protected static final int RETRY_TIMES = 3;

    private final ExecutorService executorService;

    private long timeOut;

    private TimeUnit timeOutUnit;

    protected AsyncUseCaseController(String threadName) {
        nonNullArgument(threadName, "Thread name");
        this.executorService = makeExecutorService(threadName);
        timeOut = 0;
        timeOutUnit = TimeUnit.SECONDS;
    }

    protected ExecutorService makeExecutorService(final String threadName) {
        final var threadFactory = new NumberedThreadFactory(threadName);
        return Executors.newCachedThreadPool(threadFactory);
    }

    public abstract void checkDependencies();

    public void setTimeOut(long timeOut) {
        if(timeOut < 0) {
            throw new IllegalArgumentException("Time out must be equal or greater than zero");
        }
        this.timeOut = timeOut;
    }

    public void setTimeOutUnit(TimeUnit timeOutUnit) {
        this.timeOutUnit = nonNullArgument(timeOutUnit, "Time out unit");
    }

    protected <S, T> Future<Void> start(final Flow<S, T> flow, final S value, final Consumer<T> consumer) {
        final var startFlow = new StartFlow<>(flow, value, consumer);
        return executorService.submit(startFlow, null);
    }

    protected Future<Void> start(Runnable runnable) {
        return executorService.submit(runnable, null);
    }

    protected <S, T> void retry(final Flow<S, T> flow, final S value,  final Consumer<T> consumer) {
        retry(flow, value, consumer, RETRY_TIMES);
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
        asyncRetry(flow, value, consumer, errorMessage, RETRY_TIMES);
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
        final String formattedMessage;

        try {
            tryManageFuture(future, message, exceptionClass);
        } catch(NoSuchMethodException | InstantiationException | IllegalAccessException |
                InvocationTargetException noMethodEx) {
            formattedMessage = String.format("Not valid exception %s because %s",
                    exceptionClass.getName(), noMethodEx.getMessage());
            throw new RuntimeException(formattedMessage, noMethodEx);
        }
    }

    <X extends RuntimeException> void tryManageFuture(final Future<Void> future, String message,
            Class<X> exceptionClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        final String formattedMessage;
        final Throwable cause;
        final X exception;

        try {
            if(timeOut == 0) {
                future.get();
            } else {
                future.get(timeOut, timeOutUnit);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            formattedMessage = String.format("%s: %s", message, e.getMessage());
            exception = exceptionClass.getConstructor(String.class)
                    .newInstance(formattedMessage);
            throw exception;
        } catch (ExecutionException e) {
            cause = e.getCause();
            if(exceptionClass.isInstance(cause)) {
                throw exceptionClass.cast(cause);
            }
            formattedMessage = String.format("%s: %s", message, cause.getMessage());
            exception = exceptionClass.getConstructor(String.class, Throwable.class)
                    .newInstance(formattedMessage, cause);
            throw exception;
        } catch (TimeoutException timeOutEx) {
            formattedMessage = String.format("Timeout: %s", message);
            exception = exceptionClass.getConstructor(String.class, Throwable.class)
                    .newInstance(formattedMessage, timeOutEx);
            throw exception;
        }
    }

}
