package info.cleanflow.core.builder;

import info.cleanflow.FieldRejection;
import info.cleanflow.Flow;
import info.cleanflow.Rejection;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static info.cleanflow.Objects.nonNullArgument;
import static info.cleanflow.Objects.nonNullMember;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Monadic entity builder
 * @param <S> incoming traveler type
 * @param <E> entity type
 */
public abstract class Builder<S, E> {

    private static final Logger LOG = getLogger(Builder.class);

    private static final String PREFIX_PATTERN = "%s.%s";

    private Consumer<Rejection> rejectionConsumer;

    private Supplier<E> entitySupplier;

    private S source;

    private String prefix;

    private int rejections;

    public Builder<S, E> putEntitySupplier(Supplier<E> entitySupplier) {
        this.entitySupplier = nonNullArgument(entitySupplier, "entity supplier");
        return this;
    }

    public Builder<S, E> putRejectionConsumer(Consumer<Rejection> rejectionConsumer) {
        this.rejectionConsumer = rejectionConsumer;
        return this;
    }

    public Builder<S, E> putSource(S source) {
        this.source = source;
        return this;
    }

    public Builder<S, E> putPrefix(String prefix) {
        if(prefix == null || prefix.isBlank()) {
            LOG.debug("Not assigned prefix");
            return this;
        }
        this.prefix = prefix;
        return this;
    }

    public void build(Consumer<E> entityConsumer) {
        final E entity;

        check();
        nonNullArgument(entityConsumer, "The entity consumer is a mandatory parameter for build");
        entity = entitySupplier.get();
        assemble(source, entity);
        if(rejections == 0) {
            entityConsumer.accept(entity);
        }
    }

    protected void check() {
        nonNullMember(source, "source");
        nonNullMember(entitySupplier, "entity supplier");
    }

    /**
     * Calls for load values into the entity from the source
     * @param entity A not null entity
     * @param source A not null contract source
     */
    protected abstract void assemble(S source, E entity);

    protected <V> Consumer<V> transfer(final String fieldName, final Consumer<V> setter) {
        return x -> transfer(fieldName, x, setter);
    }

    protected <V> void transfer(final String fieldName, final V value, final Consumer<V> setter) {
        nonNullArgument(fieldName, "Field name parameter is mandatory");
        nonNullArgument(setter, "Setter parameter is mandatory");
        try {
            setter.accept(value);
        } catch(RuntimeException rex) {
            LOG.warn("Error putting value {} in field {} with message {}.",
                    value, fieldName, rex.getMessage());
            reject(fieldName, value, rex);
        }
    }

    protected void reject(final String fieldName, final Object value, final Throwable exception) {
        final String prefixedName;
        final FieldRejection rejection;

        prefixedName = applyPrefix(fieldName);
        rejection = new FieldRejectionImpl(prefixedName, value, exception);
        sendRejection(rejection);
    }

    protected String applyPrefix(String fieldName) {
        if(prefix == null) {
            return fieldName;
        }
        return String.format(PREFIX_PATTERN, prefix, fieldName);
    }

    protected void sendRejection(FieldRejection rejection) {
        nonNullArgument(rejection, "Parameter rejection is mandatory");
        rejections++;
        if(rejectionConsumer == null) {
            LOG.warn("Not published rejection: {}", rejection);
            return;
        }
        rejectionConsumer.accept(rejection);
    }

    protected <V, T> Consumer<V> transform(final String fieldName,
                                           final Consumer<T> setter,
                                           final Flow<V, T> solver) {
        return x -> transform(fieldName, x, setter, solver);
    }

    protected <V, T> void transform(final String fieldName,
                                    final V value,
                                    final Consumer<T> setter,
                                    final Flow<V, T> solver) {
        if(solver == null || value == null) {
            transfer(fieldName, null, setter);
            return;
        }
        try {
            solver.flows(value, transfer(fieldName, setter));
        } catch(RuntimeException exception) {
            reject(fieldName, value, exception);
        }
    }

}
