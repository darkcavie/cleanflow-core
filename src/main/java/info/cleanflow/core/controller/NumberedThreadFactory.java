package info.cleanflow.core.controller;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static info.cleanflow.core.Objects.nonNullArgument;

public class NumberedThreadFactory implements ThreadFactory {

    static final int MAX_NUMBER = 100;

    static final String NAME_FORMAT = "%s_%02d";

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

    String getName() {
        return String.format(NAME_FORMAT, name, counter.getAndUpdate(this::updater));
    }

    int updater(final int i) {
        if(i >= MAX_NUMBER) {
            return 0;
        }
        return i + 1;
    }

}
