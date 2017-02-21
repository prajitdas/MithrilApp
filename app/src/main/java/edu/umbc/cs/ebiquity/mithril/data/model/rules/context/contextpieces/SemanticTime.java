package edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

public class SemanticTime {
	private String inferredTime;

    public SemanticTime() {
		this.inferredTime = MithrilApplication.getContextDefaultTime();
	}

	/**
	 * @param deviceTime
	 */
    public SemanticTime(String deviceTime) {
		this.inferredTime = deviceTime;
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
        SemanticTime other = (SemanticTime) obj;
		if (inferredTime == null) {
			if (other.inferredTime != null) {
				return false;
			}
		} else if (!inferredTime.equals(other.inferredTime)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the inferredTime
	 */
	public String getInferredTime() {
		return inferredTime;
	}

    /**
	 * @param inferredTime the inferredTime to set
	 */
	public void setInferredTime(String inferredTime) {
		this.inferredTime = inferredTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((inferredTime == null) ? 0 : inferredTime.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return inferredTime;
	}
}