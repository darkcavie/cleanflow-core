package info.cleanflow.storage.memory;

class SourceImplMock
        extends KeyImplMock
        implements SourceMock{

    private final int age;

    SourceImplMock(final String name, final int age) {
        super(name);
        this.age = age;
    }

    @Override
    public int getAge() {
        return age;
    }

}
