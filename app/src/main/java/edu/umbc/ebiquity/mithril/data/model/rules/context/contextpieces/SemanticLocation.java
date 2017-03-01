package edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;

import edu.umbc.ebiquity.mithril.MithrilApplication;

public class SemanticLocation {
    private Location location;
    private Address address;
    private String inferredLocation;

    public SemanticLocation(String inferredLocation, Location location, Address address) {
        this.inferredLocation = inferredLocation;
        this.location = location;
        this.address = address;
    }

    public SemanticLocation(Address address, Location location, Context context) {
        this.address = address;
        this.location = location;

        SharedPreferences sharedPreferences = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        String homeLocation = sharedPreferences.getString(MithrilApplication.getPrefHomeLocationKey(), null);
//        String workLocation = sharedPreferences.getString(MithrilApplication.getPrefWorkLocationKey(), null);
        if (address.getPostalCode().equals(homeLocation))
            this.inferredLocation = "home";
        else
            this.inferredLocation = "work";
    }

    public SemanticLocation(String inferredLocation) {
        this.inferredLocation = inferredLocation;
        this.location = null;
        this.address = null;
    }

    public SemanticLocation() {
        this.inferredLocation = MithrilApplication.getContextDefaultWorkLocation();
        this.location = null;
        this.address = null;
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
        SemanticLocation other = (SemanticLocation) obj;
        if (inferredLocation == null) {
            if (other.inferredLocation != null) {
                return false;
            }
        } else if (!inferredLocation.equals(other.inferredLocation)) {
            return false;
        }
        return true;
    }

    /**
     * @return the inferredLocation
     */
    public String getInferredLocation() {
        return inferredLocation;
    }

    /**
     * @param inferredLocation the inferredLocation to set
     */
    public void setInferredLocation(String inferredLocation) {
        this.inferredLocation = inferredLocation;
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
                + ((inferredLocation == null) ? 0 : inferredLocation.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return inferredLocation;
    }
}