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
 * The Builder class is a monadic entity builder that assembles an entity from a source.
 *
 * @param <S> the type of the incoming traveler
 * @param <E> the type of the entity
 */
public abstract class Builder<S, E> {

    private static final Logger LOG = getLogger(Builder.class);

    private static final String PREFIX_PATTERN = "%s.%s";

    private Consumer<Rejection> rejectionConsumer;

    private Supplier<E> entitySupplier;

    private S source;

    private String prefix;

    private int rejections;

    /**
     * Sets the entity supplier for the builder.
     *
     * @param entitySupplier the supplier that provides instances of the entity
     * @return the builder instance
     */
    public Builder<S, E> putEntitySupplier(Supplier<E> entitySupplier) {
        this.entitySupplier = nonNullArgument(entitySupplier, "entity supplier");
        return this;
    }

    /**
     * Sets the rejection consumer for the builder.
     *
     * @param rejectionConsumer the consumer that handles rejections
     * @return the builder instance
     */
    public Builder<S, E> putRejectionConsumer(Consumer<Rejection> rejectionConsumer) {
        this.rejectionConsumer = rejectionConsumer;
        return this;
    }

    /**
     * Sets the source for the builder.
     *
     * @param source the source data for the builder
     * @return the builder instance
     */
    public Builder<S, E> putSource(S source) {
        this.source = source;
        return this;
    }

    /**
     * Sets the prefix for the builder.
     *
     * @param prefix the prefix to be applied to field names
     * @return the builder instance
     */
    public Builder<S, E> putPrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            LOG.debug("Not assigned prefix");
            return this;
        }
        this.prefix = prefix;
        return this;
    }

    /**
     * Builds the entity using the provided entity consumer.
     *
     * @param entityConsumer the consumer that handles the built entity
     */
    public void build(Consumer<E> entityConsumer) {
        final E entity;

        check();
        nonNullArgument(entityConsumer, "The entity consumer is a mandatory parameter for build");
        entity = entitySupplier.get();
        assemble(source, entity);
        if (rejections == 0) {
            entityConsumer.accept(entity);
        }
    }

    /**
     * Performs checks to ensure that necessary parameters are set.
     * Throws exceptions if any of the required parameters are missing.
     */
    protected void check() {
        nonNullMember(source, "source");
        nonNullMember(entitySupplier, "entity supplier");
    }

    /**
     * Assembles the entity from the source data.
     *
     * @param source the source data
     * @param entity the entity to be assembled
     */
    protected abstract void assemble(S source, E entity);

    /**
     * Creates a consumer that transfers a value to a setter, with the field name specified.
     *
     * @param fieldName the name of the field to transfer the value to
     * @param setter    the setter consumer
     * @param <V>       the type of the value to be transferred
     * @return a consumer that performs the value transfer
     */
    protected <V> Consumer<V> transfer(final String fieldName, final Consumer<V> setter) {
        return x -> transfer(fieldName, x, setter);
    }

    /**
     * Transfers a value to a setter, with the field name specified.
     *
     * @param fieldName the name of the field to transfer the value to
     * @param value     the value to be transferred
     * @param setter    the setter consumer
     * @param <V>       the type of the value to be transferred
     */
    protected <V> void transfer(final String fieldName, final V value, final Consumer<V> setter) {
        nonNullArgument(fieldName, "Field name parameter is mandatory");
        nonNullArgument(setter, "Setter parameter is mandatory");
        try {
            setter.accept(value);
        } catch (RuntimeException rex) {
            LOG.warn("Error putting value {} in field {} with message {}.",
                    value, fieldName, rex.getMessage());
            reject(fieldName, value, rex);
        }
    }

    /**
     * Rejects a value with the specified field name and exception.
     *
     * @param fieldName  the name of the rejected field
     * @param value      the value that caused the rejection
     * @param exception  the exception representing the rejection
     */
    protected void reject(final String fieldName, final Object value, final Throwable exception) {
        final String prefixedName;
        final FieldRejection rejection;

        prefixedName = applyPrefix(fieldName);
        rejection = new FieldRejectionImpl(prefixedName, value, exception);
        sendRejection(rejection);
    }

    /**
     * Applies the prefix to the specified field name.
     *
     * @param fieldName the field name to apply the prefix to
     * @return the field name with the prefix applied
     */
    protected String applyPrefix(String fieldName) {
        if (prefix == null) {
            return fieldName;
        }
        return String.format(PREFIX_PATTERN, prefix, fieldName);
    }

    /**
     * Sends a rejection to the rejection consumer.
     *
     * @param rejection the rejection to be sent
     */
    protected void sendRejection(FieldRejection rejection) {
        nonNullArgument(rejection, "Parameter rejection is mandatory");
        rejections++;
        if (rejectionConsumer == null) {
            LOG.warn("Not published rejection: {}", rejection);
            return;
        }
        rejectionConsumer.accept(rejection);
    }

    /**
     * Creates a consumer that transforms a value using a solver and transfers it to a setter,
     * with the field name specified.
     *
     * @param fieldName the name of the field to transfer the transformed value to
     * @param setter    the setter consumer
     * @param solver    the flow that transforms the value
     * @param <V>       the type of the value to be transformed
     * @param <T>       the type of the transformed value
     * @return a consumer that performs the value transformation and transfer
     */
    protected <V, T> Consumer<V> transform(final String fieldName,
                                           final Consumer<T> setter,
                                           final Flow<V, T> solver) {
        return x -> transform(fieldName, x, setter, solver);
    }

    /**
     * Transforms a value using a solver and transfers it to a setter,
     * with the field name specified.
     *
     * @param fieldName the name of the field to transfer the transformed value to
     * @param value     the value to be transformed
     * @param setter    the setter consumer
     * @param solver    the flow that transforms the value
     * @param <V>       the type of the value to be transformed
     * @param <T>       the type of the transformed value
     */
    protected <V, T> void transform(final String fieldName,
                                    final V value,
                                    final Consumer<T> setter,
                                    final Flow<V, T> solver) {
        if (solver == null || value == null) {
            transfer(fieldName, null, setter);
            return;
        }
        try {
            solver.flows(value, transfer(fieldName, setter));
        } catch (RuntimeException exception) {
            reject(fieldName, value, exception);
        }
    }

}
