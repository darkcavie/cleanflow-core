package info.cleanflow.core.builder;

import info.cleanflow.core.Flow;
import org.slf4j.Logger;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Monadic entity builder
 * @param <S> incoming traveler type
 * @param <E> entity type
 */
public abstract class AbstractBuilder<S, E> {

    private static final Logger LOG = getLogger(AbstractBuilder.class);

    private static final String PREFIX_PATTERN = "%s.%s";

    private Consumer<Rejection> rejectionConsumer;

    private Supplier<E> entitySupplier;

    private S source;

    private String prefix;

    private int rejections;

    public AbstractBuilder<S, E> putEntitySupplier(Supplier<E> entitySupplier) {
        this.entitySupplier = requireNonNull(entitySupplier, "The entity supplier is mandatory");
        return this;
    }

    public AbstractBuilder<S, E> putRejectionConsumer(Consumer<Rejection> rejectionConsumer) {
        this.rejectionConsumer = rejectionConsumer;
        return this;
    }

    public AbstractBuilder<S, E> putSource(S source) {
        this.source = source;
        return this;
    }

    public AbstractBuilder<S, E> putPrefix(String prefix) {
        if(prefix.isBlank()) {
            LOG.warn("Not assigned prefix");
            return this;
        }
        this.prefix = prefix;
        return this;
    }

    public void build(Consumer<E> entityConsumer) {
        final E entity;

        check();
        requireNonNull(entityConsumer, "The entity consumer is a mandatory parameter for build");
        entity = entitySupplier.get();
        assemble(source, entity);
        if(rejections == 0) {
            entityConsumer.accept(entity);
        }
    }

    protected void check() {
        requireNonNull(source, "The source is mandatory to build");
        requireNonNull(entitySupplier, "The entity supplier is mandatory to build");
    }

    /**
     * Calls for load values into the entity from the source
     * @param entity A not null entity
     * @param source A not null contract source
     */
    protected abstract void assemble(S source, E entity);

    protected <V> Consumer<V> put(final String fieldName, final Consumer<V> setter) {
        return x -> put(fieldName, x, setter);
    }

    protected <V> void put(final String fieldName, final V value, final Consumer<V> setter) {
        requireNonNull(fieldName, "Field name parameter is mandatory");
        requireNonNull(setter, "Setter parameter is mandatory");
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
        final Rejection rejection;

        prefixedName = doPrefix(fieldName);
        rejection = new RejectionImpl(prefixedName, value, exception);
        sendRejection(rejection);
    }

    protected String doPrefix(String fieldName) {
        if(prefix == null) {
            return fieldName;
        }
        return String.format(PREFIX_PATTERN, prefix, fieldName);
    }

    protected void sendRejection(Rejection rejection) {
        requireNonNull(rejection, "Parameter rejection is mandatory");
        rejections++;
        if(rejectionConsumer == null) {
            LOG.error("Not published rejection: {}", rejection);
            return;
        }
        rejectionConsumer.accept(rejection);
    }

    protected <V, T> Consumer<V> solve(final String fieldName, final Consumer<T> setter,
                                       final Flow<V, T> solver) {
        return x -> solve(fieldName, x, setter, solver);
    }

    protected <V, T> void solve(final String fieldName, final V value,
                                final Consumer<T> setter, final Flow<V, T> solver) {
        if(solver == null || value == null) {
            put(fieldName, null, setter);
            return;
        }
        try {
            solver.flows(value, put(fieldName, setter));
        } catch(RuntimeException exception) {
            reject(fieldName, value, exception);
        }
    }

}
