package edu.umbc.cs.ebiquity.mithril.data.model;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

public class Violation {
	private int Id;
	private String violationDescription;
	private int policyId;
	private int ruleId;
	private boolean violationMarker;

	public Violation() {
		this.Id = -1;
		this.violationDescription = MithrilApplication.getConstDefaultDescription();
		this.policyId = -1;
		this.ruleId = -1;
		this.violationMarker = false;
	}

	/**
	 * @param violationDescription
	 * @param policyId
	 * @param ruleId
	 */
	public Violation(String violationDescription,
			int policyId, int ruleId, boolean violationMarker) {
		this.Id = -1;
		this.violationDescription = violationDescription;
		this.policyId = policyId;
		this.ruleId = ruleId;
		this.violationMarker = violationMarker;
	}

	/**
	 * @param violationId
	 * @param violationDescription
	 * @param policyId
	 * @param ruleId
	 */
	public Violation(int violationId, String violationDescription,
			int policyId, int ruleId, boolean violationMarker) {
		this.Id = violationId;
		this.violationDescription = violationDescription;
		this.policyId = policyId;
		this.ruleId = ruleId;
		this.violationMarker = violationMarker;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Violation other = (Violation) obj;
		if (policyId != other.policyId)
			return false;
		if (ruleId != other.ruleId)
			return false;
		if (violationDescription == null) {
			if (other.violationDescription != null)
				return false;
		} else if (!violationDescription.equals(other.violationDescription))
			return false;
		if (Id != other.Id)
			return false;
		if (violationMarker != other.violationMarker)
			return false;
		return true;
	}

	public int getPolicyId() {
		return policyId;
	}

	public int getRuleId() {
		return ruleId;
	}

	public String getViolationDescription() {
		return violationDescription;
	}

	public int getId() {
		return Id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + policyId;
		result = prime * result + ruleId;
		result = prime
				* result
				+ ((violationDescription == null) ? 0 : violationDescription
						.hashCode());
		result = prime * result + Id;
		result = prime * result + (violationMarker ? 1231 : 1237);
		return result;
	}

	public boolean isViolationMarker() {
		return violationMarker;
	}	
	public void setPolicyId(int policyId) {
		this.policyId = policyId;
	}
	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}
	public void setViolationDescription(String violationDescription) {
		this.violationDescription = violationDescription;
	}
	public void setId(int violationId) {
		this.Id = violationId;
	}

	public void setViolationMarker(boolean violationMarker) {
		this.violationMarker = violationMarker;
	}

	@Override
	public String toString() {
		return violationDescription;
	}
}