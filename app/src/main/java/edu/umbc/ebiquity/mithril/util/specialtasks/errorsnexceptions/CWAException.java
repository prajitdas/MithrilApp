package edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions;

/*
 * Created by prajit on 2/20/17.
 */

public class CWAException extends Exception {
    public CWAException() {
        super();
    }

    public CWAException(String message) {
        super(message);
    }

    public CWAException(String message, Throwable cause) {
        super(message, cause);
    }

    public CWAException(Throwable cause) {
        super(cause);
    }
}
