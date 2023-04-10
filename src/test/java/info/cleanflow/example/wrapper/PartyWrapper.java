package info.cleanflow.example.wrapper;

import info.cleanflow.example.entity.Party;
import info.cleanflow.example.sources.PartyKey;
import info.cleanflow.example.sources.PartySource;

import java.time.LocalDate;
import java.util.Optional;

public class PartyWrapper implements PartySource {

    private final Party party;

    public PartyWrapper(Party party) {
        this.party = party;
    }

    @Override
    public String getName() {
        return party.getName();
    }

    @Override
    public String getStart() {
        return party.getStart().toString();
    }

    @Override
    public Optional<String> optEnd() {
        return Optional.ofNullable(party.getEnd())
                .map(LocalDate::toString);
    }

    @Override
    public int compareTo(PartyKey o) {
        return party.getName().compareTo(o.getName());
    }

}
