package edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

public class InferredLocation {
	private String inferredLocation;

	public InferredLocation() {
		this.inferredLocation = MithrilApplication.getConstContextDefaultLocation();
	}

	/**
	 * @param inferredLocation
	 */
	public InferredLocation(String inferredLocation) {
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
		InferredLocation other = (InferredLocation) obj;
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

	/**
	 * @param inferredLocation the inferredLocation to set
	 */
	public void setInferredLocation(String inferredLocation) {
		this.inferredLocation = inferredLocation;
	}

	@Override
	public String toString() {
		return inferredLocation;
	}
}