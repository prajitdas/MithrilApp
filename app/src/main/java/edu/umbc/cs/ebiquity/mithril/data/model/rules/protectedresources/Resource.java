package edu.umbc.cs.ebiquity.mithril.data.model.rules.protectedresources;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

public class Resource {
	private int id;
	private String resourceName;
	public Resource() {
		super();
		this.id = -1;
        this.resourceName = MithrilApplication.getContextArrayResourceCategory()[1];
    }
	public Resource(int resourceId, String resourceName) {
		super();
		this.id = resourceId;
		this.resourceName = resourceName;
	}
	public Resource(String resourceName) {
		super();
		this.id = -1;
		this.resourceName = resourceName;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (id != other.id)
			return false;
		if (resourceName == null) {
			if (other.resourceName != null)
				return false;
		} else if (!resourceName.equals(other.resourceName))
			return false;
		return true;
	}
	public int getId() {
		return id;
	}

    public void setId(int id) {
        this.id = id;
    }

	public String getResourceName() {
		return resourceName;
	}

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((resourceName == null) ? 0 : resourceName.hashCode());
		return result;
	}

    @Override
	public String toString() {
		return resourceName;
	}
}