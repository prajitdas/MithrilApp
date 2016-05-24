package edu.umbc.cs.ebiquity.mithril.data.model.rules.actions;

public enum Action {
	ALLOW(2), ALLOW_WITH_CAVEAT(1), DENY(0);
	
	private int statusCode;
	
	private Action(int actionString) {
		statusCode = actionString;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
}