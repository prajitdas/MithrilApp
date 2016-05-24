package edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces;

import java.util.ArrayList;
import java.util.List;

public class PresenceInfo {
	private List<Identity> listOfUsersInTheVicinity;

	public PresenceInfo(List<Identity> aListOfUsersInTheVicinity) {
		listOfUsersInTheVicinity = new ArrayList<Identity>();
		setListOfUsersInTheVicinity(aListOfUsersInTheVicinity);
	}

	public PresenceInfo() {
		listOfUsersInTheVicinity = new ArrayList<Identity>();
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
		if (!(obj instanceof PresenceInfo)) {
			return false;
		}
		PresenceInfo other = (PresenceInfo) obj;
		if (listOfUsersInTheVicinity == null) {
			if (other.listOfUsersInTheVicinity != null) {
				return false;
			}
		} else if (!listOfUsersInTheVicinity
				.equals(other.listOfUsersInTheVicinity)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the listOfUsersInTheVicinity
	 */
	public List<Identity> getListOfUsersInTheVicinity() {
		return listOfUsersInTheVicinity;
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
				+ ((listOfUsersInTheVicinity == null) ? 0
						: listOfUsersInTheVicinity.hashCode());
		return result;
	}

	/**
	 * @param listOfUsersInTheVicinity the listOfUsersInTheVicinity to set
	 */
	public void setListOfUsersInTheVicinity(
			List<Identity> listOfUsersInTheVicinity) {
		this.listOfUsersInTheVicinity = listOfUsersInTheVicinity;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		for(Identity anIdentity : listOfUsersInTheVicinity)
			result.append(anIdentity.toString());
		return result.toString();
	}
}