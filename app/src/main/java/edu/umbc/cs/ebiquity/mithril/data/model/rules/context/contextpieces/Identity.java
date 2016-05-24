package edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

public class Identity {
	private String identity;

	public Identity() {
		identity = MithrilApplication.getConstContextDefaultIdentity();
	}

	public Identity(String anIdentity) {
		identity = anIdentity;
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
		if (!(obj instanceof Identity)) {
			return false;
		}
		Identity other = (Identity) obj;
		if (identity == null) {
			if (other.identity != null) {
				return false;
			}
		} else if (!identity.equals(other.identity)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the identity
	 */
	public String getIdentity() {
		return identity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identity == null) ? 0 : identity.hashCode());
		return result;
	}

	/**
	 * @param identity the identity to set
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}

	@Override
	public String toString() {
		return identity;
	}
}