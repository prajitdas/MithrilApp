package edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions;

/*
 * Created by prajit on 2/20/17.
 */

public class SemanticInconsistencyException extends Exception {
    public SemanticInconsistencyException() {
        super();
    }

    public SemanticInconsistencyException(String message) {
        super(message);
    }

    public SemanticInconsistencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public SemanticInconsistencyException(Throwable cause) {
        super(cause);
    }
}
