package edu.umbc.ebiquity.mithril.data.model;

import java.sql.Timestamp;

import edu.umbc.ebiquity.mithril.MithrilAC;

/**
 * Created by prajit on 7/4/17.
 */

public class Upload {
    private Timestamp time;
    private String data;

    public Upload(Timestamp time, String data) {
        setTime(time);
        setData(data);
    }

    @Override
    public String toString() {
        return "Upload time: " + MithrilAC.getTimeText(true, getTime());
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
