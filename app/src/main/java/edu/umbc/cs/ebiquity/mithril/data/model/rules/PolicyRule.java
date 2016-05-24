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
	private UserContext context;
	private RuleAction action;
	public PolicyRule() {
		super();
		this.id = -1;
		this.name = MithrilApplication.getConstPolRulDefaultRule();
		this.requester = new Requester();
		this.resource = new Resource();
		this.context = new UserContext(new PresenceInfo(new ArrayList<Identity>()),
				new InferredActivity(), new Identity(), new InferredLocation(), new DeviceTime());
		this.action = new RuleAction(Action.DENY);
	}
	public PolicyRule(int policyRuleId, String name, Requester requester, Resource resource,
			UserContext context, RuleAction action) {
		super();
		this.id = policyRuleId;
		this.name = name;
		this.requester = requester;
		this.resource = resource;
		this.context = context;
		this.action = action;
	}
	public PolicyRule(String name, Requester requester, Resource resource,
			UserContext context, RuleAction action) {
		super();
		this.id = -1;
		this.name = name;
		this.requester = requester;
		this.resource = resource;
		this.context = context;
		this.action = action;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicyRule other = (PolicyRule) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (requester == null) {
			if (other.requester != null)
				return false;
		} else if (!requester.equals(other.requester))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}
	public RuleAction getAction() {
		return action;
	}
	public UserContext getContext() {
		return context;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Requester getRequester() {
		return requester;
	}
	public Resource getResource() {
		return resource;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((requester == null) ? 0 : requester.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		return result;
	}
	public void setAction(RuleAction action) {
		this.action = action;
	}
	public void setContext(UserContext context) {
		this.context = context;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setRequester(Requester requester) {
		this.requester = requester;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	@Override
	public String toString() {
		return name;
	}
}