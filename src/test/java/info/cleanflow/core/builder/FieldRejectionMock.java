package info.cleanflow.core.builder;

import info.cleanflow.FieldRejection;

import java.util.Optional;

class FieldRejectionMock implements FieldRejection {

    @Override
    public String getField() {
        return null;
    }

    @Override
    public Throwable getException() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public String getValueString() {
        return null;
    }

    @Override
    public <T> Optional<T> optValue(Class<T> valueClass) {
        return Optional.empty();
    }
}
