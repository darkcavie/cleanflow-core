package info.cleanflow.core.builder;

import java.util.Collection;
import java.util.stream.Stream;

import static info.cleanflow.core.Objects.nonNullArgument;

/**
 * It represents an unsuccessful build of an entity. It contains the field rejections.
 */
public class InvalidBuiltException extends RuntimeException {

    private final Collection<Rejection> rejectionCollection;

    protected InvalidBuiltException(final String entityName,
                                    final Collection<Rejection> rejectionCollection) {
        super(String.format("Invalid built of %s with %d rejections",
                nonNullArgument(entityName, "Entity name"),
                nonNullArgument(rejectionCollection, "rejection collection").size()));
        this.rejectionCollection = rejectionCollection;
    }

    public Stream<Rejection> rejectionStream() {
        return rejectionCollection.stream();
    }

    public Stream<String> messageStream() {
        return rejectionCollection.stream()
                .map(Rejection::toString);
    }

}
