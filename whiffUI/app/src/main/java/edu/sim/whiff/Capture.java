package edu.sim.whiff;

import java.util.Date;

/**
 * Class for holding the information stored in the 'capture' Table
 * in the database
 *
 * @author Yeo Pei Xuan
 */

public class Capture {

    public Long ID;
    public String name;
    public String desc;
    public String fileName;
    public Integer fileSize;
    public Date startTime;
    public Date endTime;
}
