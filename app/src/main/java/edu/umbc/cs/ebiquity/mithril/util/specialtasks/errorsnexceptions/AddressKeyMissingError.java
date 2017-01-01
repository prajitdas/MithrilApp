package edu.umbc.cs.ebiquity.mithril.util.specialtasks.errorsnexceptions;

/**
 * Created by Prajit on 12/26/2016.
 */

public class AddressKeyMissingError extends Error {
    private String message;

    public AddressKeyMissingError() {
        super();
        setMessage(new String("Please specify a key to store the address in"));
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}