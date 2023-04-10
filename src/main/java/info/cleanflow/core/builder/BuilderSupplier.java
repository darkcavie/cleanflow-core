package info.cleanflow.core.builder;

import java.util.function.Supplier;

public interface BuilderSupplier<S, E> extends Supplier<Builder<S, E>> {}
