package edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions;

/**
 * Created by prajit on 4/17/17.
 */

public class UpdateAppOpsStatsException extends Exception {
    private String message;
    public UpdateAppOpsStatsException() {
        super();
    }

    public UpdateAppOpsStatsException(String message) {
        super();
        setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }

    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
