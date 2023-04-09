package info.cleanflow.core.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static info.cleanflow.core.Objects.nonNullArgument;

public class RejectionCollector implements Consumer<Rejection> {

    private final String entityName;

    private final List<Rejection> rejectionList;

    public RejectionCollector(final String entityName) {
        this.entityName = nonNullArgument(entityName, "Entity name");
        rejectionList = new ArrayList<>();
    }

    @Override
    public void accept(Rejection rejection) {
        Optional.ofNullable(rejection)
                .ifPresent(rejectionList::add);
    }

    public void checkRejections() throws InvalidBuiltException {
        if(!rejectionList.isEmpty()) {
            throw new InvalidBuiltException(entityName, rejectionList);
        }
    }

}
