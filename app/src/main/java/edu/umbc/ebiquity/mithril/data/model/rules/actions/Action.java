package edu.umbc.ebiquity.mithril.data.model.rules.actions;

public enum Action {
    ALLOW(1), ALLOW_WITH_CAVEAT(2), DENY(0);

    private int statusCode;

    Action(int actionString) {
        statusCode = actionString;
    }

    public int getStatusCode() {
        return statusCode;
    }
}