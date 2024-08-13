package info.cleanflow.example.builder;

import info.cleanflow.core.builder.Builder;
import info.cleanflow.example.entity.Party;
import info.cleanflow.example.sources.PartySource;

import java.time.LocalDate;
import java.util.function.Consumer;

public class PartyBuilder extends Builder<PartySource, Party> {

    @Override
    protected void assemble(PartySource source, Party entity) {
        transfer(PartySource.NAME_FIELD, source.getName(), entity::setName);
        transform(PartySource.START_FIELD, source.getStart(), entity::setStart, this::localDate);
        source.optEnd()
                .ifPresent(transform(PartySource.END_FIELD, entity::setEnd, this::localDate));
    }

    void localDate(String source, Consumer<LocalDate> consumer) {
        consumer.accept(LocalDate.parse(source));
    }

}
