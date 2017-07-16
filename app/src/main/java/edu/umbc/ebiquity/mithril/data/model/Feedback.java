package edu.umbc.ebiquity.mithril.data.model;

import java.sql.Timestamp;

/**
 * Created by prajit on 7/4/17.
 */

public class Feedback {
    private Timestamp time;
    private int tvcount;
    private int fvcount;
    private int polcount;

    public Feedback(Timestamp time, int tvcount, int fvcount, int polcount) {
        this.time = time;
        this.tvcount = tvcount;
        this.fvcount = fvcount;
        this.polcount = polcount;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getTvcount() {
        return tvcount;
    }

    public void setTvcount(int tvcount) {
        this.tvcount = tvcount;
    }

    public int getFvcount() {
        return fvcount;
    }

    public void setFvcount(int fvcount) {
        this.fvcount = fvcount;
    }

    public int getPolcount() {
        return polcount;
    }

    public void setPolcount(int polcount) {
        this.polcount = polcount;
    }
}
