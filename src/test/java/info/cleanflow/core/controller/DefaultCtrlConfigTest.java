package info.cleanflow.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCtrlConfigTest {

    private DefaultCtrlConfig config;

    @BeforeEach
    void setUp() {
        config = new DefaultCtrlConfig();
    }

    @Test
    void getTimeOut() {
        assertEquals(0, config.getTimeOut());
    }

    @Test
    void getTimeOutUnit() {
        assertEquals(TimeUnit.SECONDS, config.getTimeOutUnit());
    }

    @Test
    void getRetryTimes() {
        assertEquals(3, config.getRetryTimes());
    }

}