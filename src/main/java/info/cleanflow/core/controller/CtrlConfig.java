package info.cleanflow.core.controller;

import java.util.concurrent.TimeUnit;

public interface CtrlConfig {

    long getTimeOut();

    TimeUnit getTimeOutUnit();

    int getRetryTimes();

}
