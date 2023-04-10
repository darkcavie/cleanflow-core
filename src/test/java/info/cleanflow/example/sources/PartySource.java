package info.cleanflow.example.sources;

import java.util.Optional;

public interface PartySource extends PartyKey {

    String START_FIELD = "start";

    String END_FIELD = "end";

    String getStart();

    Optional<String> optEnd();

}
