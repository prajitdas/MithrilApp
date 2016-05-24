package edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

public class InferredActivity {
	private String inferredActivity;

	public InferredActivity() {
		this.inferredActivity = MithrilApplication.getConstContextDefaultActivity();
	}

	/**
	 * @param inferredActivity
	 */
	public InferredActivity(String inferredActivity) {
		this.inferredActivity = inferredActivity;
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
		InferredActivity other = (InferredActivity) obj;
		if (inferredActivity == null) {
			if (other.inferredActivity != null) {
				return false;
			}
		} else if (!inferredActivity.equals(other.inferredActivity)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the inferredActivity
	 */
	public String getInferredActivity() {
		return inferredActivity;
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
				+ ((inferredActivity == null) ? 0 : inferredActivity.hashCode());
		return result;
	}

	/**
	 * @param inferredActivity the inferredActivity to set
	 */
	public void setInferredActivity(String inferredActivity) {
		this.inferredActivity = inferredActivity;
	}

	@Override
	public String toString() {
		return inferredActivity;
	}
}