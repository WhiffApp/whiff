package edu.sim.whiff;

import java.util.Date;


public class CaptureItem {

    public Long     ID;
    public Long     captureID;
    public Date     timestamp;
    public String   sourceAddress;
    public Integer  sourcePort;
    public String   destinationAddress;
    public Integer  destinationPort;
    public String   protocol;
    public Integer  length;
}
