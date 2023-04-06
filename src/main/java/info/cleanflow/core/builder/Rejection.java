package info.cleanflow.core.builder;

import java.util.Optional;

public interface Rejection {

    String getField();

    Throwable getException();

    String getMessage();

    String getValueString();

    <T> Optional<T> optValue(Class<T> valueClass);

}
