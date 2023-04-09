package info.cleanflow.storage.memory;

import info.cleanflow.Objects;

class KeyImplMock implements KeyMock {

    private final String name;

    KeyImplMock(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int compareTo(KeyMock o) {
        return name.compareTo(o.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equalsByComparable(KeyMock.class, this, obj);
    }

}
