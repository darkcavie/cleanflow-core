package info.cleanflow.core.builder.example;

import java.util.Optional;

class ExampleSourceImpl implements ExampleSource {

    public String getName() {
        return "mockName";
    }

    @Override
    public Optional<String> optStart() {
        return Optional.of("2023-01-01T10:00:00");
    }

}
