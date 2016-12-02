package edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

public class SemanticLocation {
    private String inferredLocation;

    public SemanticLocation() {
        this.inferredLocation = MithrilApplication.getConstContextDefaultLocation();
	}

	/**
	 * @param inferredLocation
	 */
    public SemanticLocation(String inferredLocation) {
        this.inferredLocation = inferredLocation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
        SemanticLocation other = (SemanticLocation) obj;
        if (inferredLocation == null) {
			if (other.inferredLocation != null) {
				return false;
			}
		} else if (!inferredLocation.equals(other.inferredLocation)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the inferredLocation
	 */
	public String getInferredLocation() {
		return inferredLocation;
	}

    /**
     * @param inferredLocation the inferredLocation to set
     */
    public void setInferredLocation(String inferredLocation) {
        this.inferredLocation = inferredLocation;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((inferredLocation == null) ? 0 : inferredLocation.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return inferredLocation;
	}
}