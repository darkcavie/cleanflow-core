package info.cleanflow.core.controller;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static info.cleanflow.Objects.nonNullArgument;

public class NumberedThreadFactory implements ThreadFactory {

    public static final int MAX_NUMBER = 100;

    public static final String NAME_FORMAT = "%s_%02d";

    private final String name;

    private final AtomicInteger counter;

    public NumberedThreadFactory(String name) {
        this.name = nonNullArgument(name, "Thread name");
        counter = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, getName());
    }

    protected String getName() {
        final int number = counter.getAndUpdate(this::updater);
        return String.format(NAME_FORMAT, name, number);
    }

    protected int updater(final int i) {
        if(i >= MAX_NUMBER) {
            return 0;
        }
        return i + 1;
    }

}
