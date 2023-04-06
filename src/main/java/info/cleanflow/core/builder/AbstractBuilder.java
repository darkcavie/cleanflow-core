package info.cleanflow.core.builder;

import org.slf4j.Logger;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Monadic entity builder
 * @param <E> entity type
 * @param <T> incoming traveler type
 */
public abstract class AbstractBuilder<E, T> {

    private static final Logger LOG = getLogger(AbstractBuilder.class);

    private static final String PREFIX_PATTERN = "%s.%s";

    private Consumer<Rejection> rejectionConsumer;

    private Supplier<E> entitySupplier;

    private T source;

    private String prefix;

    public AbstractBuilder<E, T> putEntitySupplier(Supplier<E> entitySupplier) {
        this.entitySupplier = requireNonNull(entitySupplier, "The entity supplier is mandatory");
        return this;
    }

    public AbstractBuilder<E, T> putRejectionConsumer(Consumer<Rejection> rejectionConsumer) {
        this.rejectionConsumer = rejectionConsumer;
        return this;
    }

    public AbstractBuilder<E, T> putSource(T source) {
        this.source = source;
        return this;
    }

    public AbstractBuilder<E, T> putPrefix(String prefix) {
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
        assemble(entity, source);
        entityConsumer.accept(entity);
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
    protected abstract void assemble(E entity, T source);

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
        if(rejectionConsumer == null) {
            LOG.error("Not published rejection: {}", rejection);
            return;
        }
        rejectionConsumer.accept(rejection);
    }


    protected void putInstantFromMillis(final String fieldName, final long millis, final Consumer<Instant> setter) {
        final Instant instant;

        instant = Instant.ofEpochMilli(millis);
        put(fieldName, instant, setter);
    }

    protected void putLocalDate(final String fieldName, final String date, final Consumer<LocalDate> setter) {
        final LocalDate localDate;

        try {
            localDate = LocalDate.parse(date);
            put(fieldName, localDate, setter);
        } catch(DateTimeParseException exception) {
            reject(fieldName, date, exception);
        }
    }

    protected void putLocalDateTime(final String fieldName, final String dateTime,
                                    final Consumer<LocalDateTime> setter) {
        final LocalDateTime localDateTime;

        try {
            localDateTime = LocalDateTime.parse(dateTime);
            put(fieldName, localDateTime, setter);
        } catch(DateTimeParseException exception) {
            reject(fieldName, dateTime, exception);
        }
    }

}
