package com.app.whiff.whiff.UI.NonRootScanner;

/**
 * Created by danie on 30/1/2018.
 */

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
