package info.cleanflow.core.builder.example;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleEntity {

    void setName(String name) {
        if(name == null) {
            throw new IllegalArgumentException("Name can not be null");
        }
        assertEquals("mockName", name);
    }

    void setStart(LocalDateTime start) {
        if(start == null) {
            throw new IllegalArgumentException("start can not be null");
        }
        assertEquals(2023, start.getYear());
    }

}
