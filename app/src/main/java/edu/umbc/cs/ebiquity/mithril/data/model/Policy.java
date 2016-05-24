package edu.umbc.cs.ebiquity.mithril.data.model;

import java.util.ArrayList;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.PolicyRule;

public class Policy {
	private ArrayList<PolicyRule> policyRules;
	
	public Policy() {
		policyRules = new ArrayList<PolicyRule>();
	}
	
	public void addActionToPolicy(PolicyRule aUserAction) {
		policyRules.add(aUserAction);
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
		Policy other = (Policy) obj;
		if (policyRules == null) {
			if (other.policyRules != null) {
				return false;
			}
		} else if (!policyRules.equals(other.policyRules)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the policyRules
	 */
	public ArrayList<PolicyRule> getPolicyRules() {
		return policyRules;
	}

	public ArrayList<String> getStringOfRules() {
		ArrayList<String> tempRulesStringList = new ArrayList<String>();
		for(PolicyRule aPolicyRule : policyRules)
			tempRulesStringList.add(aPolicyRule.toString());
		return tempRulesStringList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((policyRules == null) ? 0 : policyRules.hashCode());
		return result;
	}

	/**
	 * @param policyRules the policyRules to set
	 */
	public void setPolicyRules(ArrayList<PolicyRule> policyRules) {
		this.policyRules = policyRules;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Policy [policyRules=" + policyRules + "]";
	}
}