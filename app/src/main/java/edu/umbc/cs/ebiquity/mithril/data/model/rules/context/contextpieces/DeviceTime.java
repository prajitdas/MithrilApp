package edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

public class DeviceTime {
	private String deviceTime;

	public DeviceTime() {
		this.deviceTime = MithrilApplication.getConstContextDefaultTime();
	}

	/**
	 * @param deviceTime
	 */
	public DeviceTime(String deviceTime) {
		this.deviceTime = deviceTime;
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
		DeviceTime other = (DeviceTime) obj;
		if (deviceTime == null) {
			if (other.deviceTime != null) {
				return false;
			}
		} else if (!deviceTime.equals(other.deviceTime)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the deviceTime
	 */
	public String getDeviceTime() {
		return deviceTime;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deviceTime == null) ? 0 : deviceTime.hashCode());
		return result;
	}

	/**
	 * @param deviceTime the deviceTime to set
	 */
	public void setDeviceTime(String deviceTime) {
		this.deviceTime = deviceTime;
	}

	@Override
	public String toString() {
		return deviceTime;
	}
}