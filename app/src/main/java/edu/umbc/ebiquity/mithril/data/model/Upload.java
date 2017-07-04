package edu.umbc.ebiquity.mithril.data.model;

import java.sql.Timestamp;

import edu.umbc.ebiquity.mithril.MithrilAC;

/**
 * Created by prajit on 7/4/17.
 */

public class Upload {
    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    private Timestamp uploadTime;

    public Upload(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return "Upload time: " + MithrilAC.getTimeText(true, uploadTime);
    }
}
