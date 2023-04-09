package info.cleanflow.core;

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

}
