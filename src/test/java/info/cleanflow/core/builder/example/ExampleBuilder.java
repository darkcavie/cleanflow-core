package info.cleanflow.core.builder.example;

import info.cleanflow.core.builder.Builder;

import java.time.LocalDateTime;
import java.util.function.Consumer;

class ExampleBuilder extends Builder<ExampleSource, ExampleEntity> {

    @Override
    protected void assemble(ExampleSource source, ExampleEntity entity) {
        transfer("name", source.getName(), entity::setName);
        source.optStart().ifPresent(transform("start", entity::setStart, this::toLocalDateTime));
    }

    void toLocalDateTime(String value, Consumer<LocalDateTime> targetConsumer) {
        targetConsumer.accept(LocalDateTime.parse(value));
    }

}
