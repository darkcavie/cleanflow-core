package info.cleanflow.core.builder;

import info.cleanflow.example.builder.PartyBuilder;
import info.cleanflow.example.entity.Party;
import info.cleanflow.example.sources.PartySource;
import info.cleanflow.example.sources.PartySourceMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class PartyBuilderTest {

    private Builder<PartySource, Party> builder;

    private PartySourceMock partySourceMock;

    private int consumed;

    @BeforeEach
    void setup() {
        partySourceMock = new PartySourceMock();
        builder = new PartyBuilder()
                .putEntitySupplier(Party::new);
        consumed = 0;
    }

    @Test
    void build() {
        final Consumer<Party> targetConsumer;
        final PartySource source;

        targetConsumer = e -> {
            consumed++;
            assertNotNull(e);
            assertEquals("mockName", e.getName());
            assertNotNull(e.getStart());
            assertNull(e.getEnd());
        };
        source = partySourceMock.partySource("mockName", LocalDate.now().toString(), null);
        assertDoesNotThrow(() -> builder
                .putSource(source)
                .build(targetConsumer));
        assertEquals(1, consumed);
    }

}
