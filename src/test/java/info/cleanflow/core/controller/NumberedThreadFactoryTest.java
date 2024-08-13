package info.cleanflow.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberedThreadFactoryTest {

    private NumberedThreadFactory impl;

    @BeforeEach
    void setUp() {
        impl = new NumberedThreadFactory("mockName");
    }

    @Test
    void updaterMaxReached() {
        assertEquals(0, impl.updater(NumberedThreadFactory.MAX_NUMBER));
    }

    @Test
    void getName() {
        assertEquals("mockName_00", impl.getName());
    }

}