package info.cleanflow.core.builder;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

class RejectionImpl implements Rejection {

    private final String field;

    private final Object value;

    private final Throwable exception;

    RejectionImpl(final String field, final Object value, final Throwable exception) {
        this.field = requireNonNull(field, "The field is mandatory for rejection");
        this.value = value;
        this.exception = requireNonNull(exception, "The exception is mandatory for rejection");
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public String getMessage() {
        return exception.getMessage();
    }

    @Override
    public String getValueString() {
        return Optional.ofNullable(value)
                .map(Object::toString)
                .orElse("[null value]");
    }

    @Override
    public <T> Optional<T> optValue(Class<T> valueClass) {
        requireNonNull(valueClass, "The class is a mandatory argument");
        return Optional.ofNullable(value)
                .filter(valueClass::isInstance)
                .map(valueClass::cast);
    }

    @Override
    public String toString() {
        return String.format("Rejected value [%s] in field '%s' with message '%s'",
                getValueString(), field, getMessage());
    }

}
