package info.cleanflow;

import java.util.Optional;

public interface FieldRejection extends Rejection {

    String getField();

    String getValueString();

    <T> Optional<T> optValue(Class<T> valueClass);

}
