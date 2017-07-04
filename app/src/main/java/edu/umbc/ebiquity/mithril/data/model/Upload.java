package edu.umbc.ebiquity.mithril.data.model;

import java.sql.Timestamp;

import edu.umbc.ebiquity.mithril.MithrilAC;

/**
 * Created by prajit on 7/4/17.
 */

public class Upload {
    private Timestamp uploadTime;
    private String uploadedData;

    @Override
    public String toString() {
        return "Upload time: " + MithrilAC.getTimeText(true, uploadTime);
    }

    public Timestamp getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Timestamp uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadedData() {
        return uploadedData;
    }

    public void setUploadedData(String uploadedData) {
        this.uploadedData = uploadedData;
    }
}
