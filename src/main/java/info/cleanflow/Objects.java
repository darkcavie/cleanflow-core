package info.cleanflow;

import java.util.Optional;

public interface Objects {

    static <T> T nonNullArgument(T argument, String argumentName) {
        if(argument == null) {
            var message = String.format("The argument %s is mandatory", argumentName);
            throw new IllegalArgumentException(message);
        }
        return argument;
    }

    static <T> T nonNullMember(T member, String memberName) {
        if(member == null) {
            var message = String.format("The member %s is mandatory in this point", memberName);
            throw new IllegalStateException(message);
        }
        return member;
    }

    static <T extends Comparable<T>> boolean equalsByComparable(Class<T> castClass, T me, Object other) {
        nonNullArgument(me, "The calling object");
        return optCast(castClass, other)
                .map(me::compareTo)
                .filter(Objects::isZero)
                .isPresent();
    }

    static <T> Optional<T> optCast(Class<T> castClass, Object casted) {
        nonNullArgument(castClass, "The comparable class");
        return Optional.ofNullable(casted)
                .filter(castClass::isInstance)
                .map(castClass::cast);
    }

    static boolean isZero(int i) {
        return i == 0;
    }

}
