package edu.umbc.cs.ebiquity.mithril.data.model.rules.context;

import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.DeviceTime;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.Identity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredActivity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredLocation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.PresenceInfo;

public class UserContext {
	private int id;
	private PresenceInfo presenceInfo;
	private InferredActivity activity;
	private Identity identity;
	private InferredLocation location;
	private DeviceTime time;
	public UserContext(int id, PresenceInfo presenceInfo,
			InferredActivity activity, Identity identity,
			InferredLocation location, DeviceTime time) {
		this.id = id;
		this.presenceInfo = presenceInfo;
		this.activity = activity;
		this.identity = identity;
		this.location = location;
		this.time = time;
	}
	public UserContext(PresenceInfo presenceInfo,
			InferredActivity activity, Identity identity,
			InferredLocation location, DeviceTime time) {
		this.id = -1;
		this.presenceInfo = presenceInfo;
		this.activity = activity;
		this.identity = identity;
		this.location = location;
		this.time = time;
	}
	public UserContext() {
		this.id = -1;
		this.presenceInfo = new PresenceInfo();
		this.activity = new InferredActivity();
		this.identity = new Identity();
		this.location = new InferredLocation();
		this.time = new DeviceTime();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserContext other = (UserContext) obj;
		if (activity == null) {
			if (other.activity != null)
				return false;
		} else if (!activity.equals(other.activity))
			return false;
		if (id != other.id)
			return false;
		if (identity == null) {
			if (other.identity != null)
				return false;
		} else if (!identity.equals(other.identity))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (presenceInfo == null) {
			if (other.presenceInfo != null)
				return false;
		} else if (!presenceInfo.equals(other.presenceInfo))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}
	public InferredActivity getActivity() {
		return activity;
	}
	public int getId() {
		return id;
	}
	public Identity getIdentity() {
		return identity;
	}
	public InferredLocation getLocation() {
		return location;
	}
	public PresenceInfo getPresenceInfo() {
		return presenceInfo;
	}
	public DeviceTime getTime() {
		return time;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activity == null) ? 0 : activity.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((identity == null) ? 0 : identity.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result
				+ ((presenceInfo == null) ? 0 : presenceInfo.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}
	public void setActivity(InferredActivity activity) {
		this.activity = activity;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	public void setLocation(InferredLocation location) {
		this.location = location;
	}
	public void setPresenceInfo(PresenceInfo presenceInfo) {
		this.presenceInfo = presenceInfo;
	}
	public void setTime(DeviceTime time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "UserContext [id=" + id + ", presenceInfo=" + presenceInfo
				+ ", activity=" + activity + ", identity=" + identity
				+ ", location=" + location + ", time=" + time + "]";
	}
}