package com.app.whiff.whiff.NonRootScanner;

import java.util.Date;


/**
 * Class for holding the information stored in the 'capture_data' Table
 *
 * @author Yeo Pei Xuan
 */

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
    public String   text;
    public String   data;
}
