package edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions;

/**
 * Created by prajit on 4/17/17.
 */

public class AppOpsException extends Exception {
    private String message;
    public AppOpsException() {
        super();
    }

    public AppOpsException(String message) {
        super();
        setMessage(message);
    }

    @Override
    public String getMessage() {
        if(message == null)
            return super.getMessage();
        return message;
    }

    private void setMessage(String message) {
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
