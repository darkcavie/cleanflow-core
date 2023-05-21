package info.cleanflow;

import java.util.Optional;

/**
 * Utility class for common object operations.
 */
public interface Objects {

    /**
     * Ensures that the provided argument is not null.
     *
     * @param argument     the argument to check for null
     * @param argumentName the name of the argument (used for error message)
     * @param <T>          the type of the argument
     * @return the non-null argument
     * @throws IllegalArgumentException if the argument is null
     */
    static <T> T nonNullArgument(T argument, String argumentName) {
        if (argument == null) {
            var message = String.format("The argument %s is mandatory", argumentName);
            throw new IllegalArgumentException(message);
        }
        return argument;
    }

    /**
     * Ensures that the provided member is not null.
     *
     * @param member      the member to check for null
     * @param memberName  the name of the member (used for error message)
     * @param <T>         the type of the member
     * @throws IllegalStateException if the member is null
     */
    static <T> void nonNullMember(T member, String memberName) {
        if (member == null) {
            var message = String.format("The member %s is mandatory in this point", memberName);
            throw new IllegalStateException(message);
        }
    }

    /**
     * Compares the calling object with another object of the same type using their natural ordering.
     * Returns true if the objects are equal based on the comparison, false otherwise.
     *
     * @param castClass the class of the calling object for type casting
     * @param me        the calling object
     * @param other     the object to compare with
     * @param <T>       the type of the objects
     * @return true if the objects are equal based on the comparison, false otherwise
     */
    static <T extends Comparable<T>> boolean equalsByComparable(Class<T> castClass, T me, Object other) {
        nonNullArgument(me, "The calling object");
        return optCast(castClass, other)
                .map(me::compareTo)
                .filter(Objects::isZero)
                .isPresent();
    }

    /**
     * Attempts to cast an object to the specified class.
     *
     * @param castClass the target class to cast to
     * @param cast    the object to be cast
     * @param <T>       the type of the target class
     * @return an optional containing the cast object if successful, or an empty optional if the cast failed or the object is null
     */
    static <T> Optional<T> optCast(Class<T> castClass, Object cast) {
        nonNullArgument(castClass, "The comparable class");
        return Optional.ofNullable(cast)
                .filter(castClass::isInstance)
                .map(castClass::cast);
    }

    /**
     * Checks if the given integer is zero.
     *
     * @param i the integer to check
     * @return true if the integer is zero, false otherwise
     */
    static boolean isZero(int i) {
        return i == 0;
    }

}
