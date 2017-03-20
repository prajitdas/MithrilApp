package edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions;

/**
 * Created by prajit on 3/20/17.
 */

public class PhoneNotRootedException extends Exception {
    public PhoneNotRootedException() {
        super();
    }

    public PhoneNotRootedException(String message) {
        super(message);
    }

    public PhoneNotRootedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhoneNotRootedException(Throwable cause) {
        super(cause);
    }
}
