package info.cleanflow.core.controller;

import java.util.concurrent.TimeUnit;

public class DefaultCtrlConfig implements CtrlConfig {

    protected static final int RETRY_TIMES = 3;

    protected static final int DEFAULT_TIME_OUT = 0;

    @Override
    public long getTimeOut() {
        return DEFAULT_TIME_OUT;
    }

    @Override
    public TimeUnit getTimeOutUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    public int getRetryTimes() {
        return RETRY_TIMES;
    }

}
