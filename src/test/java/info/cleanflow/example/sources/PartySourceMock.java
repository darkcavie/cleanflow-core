package info.cleanflow.example.sources;

import java.util.Optional;

import static info.cleanflow.Objects.equalsByComparable;

public class PartySourceMock {

    public PartyKey partyKey(final String name) {
        return new KeyMock(name);
    }

    public PartySource partySource(final String name, final String start, final String end) {
        return new SourceMock(name, start, end);

    }

    private static class KeyMock implements PartyKey {

        private final String name;

        KeyMock(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int compareTo(PartyKey o) {
            return name.compareTo(o.getName());
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return equalsByComparable(PartyKey.class, this, obj);
        }

    }

    private static class SourceMock extends KeyMock implements PartySource {

        private final String start;

        private final String end;

        private SourceMock(String name, String start, String end) {
            super(name);
            this.start = start;
            this.end = end;
        }


        @Override
        public String getStart() {
            return start;
        }

        @Override
        public Optional<String> optEnd() {
            return Optional.ofNullable(end);
        }

    }

}
