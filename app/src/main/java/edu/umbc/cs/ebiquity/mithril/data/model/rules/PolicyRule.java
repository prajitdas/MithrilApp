package edu.umbc.cs.ebiquity.mithril.data.model.rules;

import java.util.ArrayList;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.Action;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.RuleAction;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.UserContext;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.DeviceTime;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.Identity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredActivity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredLocation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.PresenceInfo;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.protectedresources.Resource;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.requesters.Requester;

public class PolicyRule {
	private int id;
	private String name;
	private Requester requester;
	private Resource resource;
	private String context;
	private RuleAction action;
	public PolicyRule() {
		super();
		this.id = -1;
		this.name = MithrilApplication.getConstPolRulDefaultRule();
		this.requester = new Requester();
		this.resource = new Resource();
		this.context = new String();
		this.action = new RuleAction(Action.DENY);
	}
	public PolicyRule(int policyRuleId, String name, Requester requester, Resource resource,
			String context, RuleAction action) {
		super();
		this.id = policyRuleId;
		this.name = name;
		this.requester = requester;
		this.resource = resource;
		this.context = context;
		this.action = action;
	}
	public PolicyRule(String name, Requester requester, Resource resource,
			String context, RuleAction action) {
		super();
		this.id = -1;
		this.name = name;
		this.requester = requester;
		this.resource = resource;
		this.context = context;
		this.action = action;
	}

	@Override
	public String toString() {
		return "PolicyRule{" +
				"id=" + id +
				", name='" + name + '\'' +
				", requester=" + requester +
				", resource=" + resource +
				", context='" + context + '\'' +
				", action=" + action +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PolicyRule)) return false;

		PolicyRule that = (PolicyRule) o;

		if (getId() != that.getId()) return false;
		if (!getName().equals(that.getName())) return false;
		if (!getRequester().equals(that.getRequester())) return false;
		if (!getResource().equals(that.getResource())) return false;
		if (!getContext().equals(that.getContext())) return false;
		return getAction().equals(that.getAction());

	}

	@Override
	public int hashCode() {
		int result = getId();
		result = 31 * result + getName().hashCode();
		result = 31 * result + getRequester().hashCode();
		result = 31 * result + getResource().hashCode();
		result = 31 * result + getContext().hashCode();
		result = 31 * result + getAction().hashCode();
		return result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Requester getRequester() {
		return requester;
	}

	public void setRequester(Requester requester) {
		this.requester = requester;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public RuleAction getAction() {
		return action;
	}

	public void setAction(RuleAction action) {
		this.action = action;
	}
}