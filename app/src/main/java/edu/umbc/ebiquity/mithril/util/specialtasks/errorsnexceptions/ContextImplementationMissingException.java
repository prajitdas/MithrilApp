package edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions;

/**
 * Created by prajit on 6/26/17.
 */

public class ContextImplementationMissingException extends Exception {
    private String message;

    public ContextImplementationMissingException(String message) {
        super(message);
        setMessage(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
