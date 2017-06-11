package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.location.Address;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

import edu.umbc.ebiquity.mithril.MithrilApplication;

public class SemanticLocation implements Parcelable, SemanticUserContext {
    public static final Creator<SemanticLocation> CREATOR =
            new Creator<SemanticLocation>() {
                @Override
                public SemanticLocation createFromParcel(Parcel in) {
                    String inferredLocation = in.readString();
                    Location location = Location.CREATOR.createFromParcel(in);
                    Address address = Address.CREATOR.createFromParcel(in);
                    return new SemanticLocation(inferredLocation, location, address);
                }

                @Override
                public SemanticLocation[] newArray(int size) {
                    return new SemanticLocation[size];
                }
            };
    private Location location;
    private Address address;
    private String inferredLocation;
    private String type;

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    private String locationDetails;

    public SemanticLocation(String inferredLocation, Location location, Address address) {
        this.inferredLocation = inferredLocation;
        this.location = location;
        this.address = address;
    }

    public SemanticLocation(String locationkey, Location location) {
        this.location = location;
        this.inferredLocation = locationkey;
        this.address = new Address(Locale.getDefault());
    }

    public SemanticLocation(Address address, Location location, String key) {
        this.address = address;
        this.location = location;

//        SharedPreferences sharedPreferences = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
//        String homeLocation = sharedPreferences.getString(MithrilApplication.getPrefHomeLocationKey(), null);
////        String workLocation = sharedPreferences.getString(MithrilApplication.getPrefWorkLocationKey(), null);
//        if (address.getPostalCode().equals(homeLocation))
//            this.inferredLocation = "home";
//        else
        this.inferredLocation = key;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeParcelable(address, flags);
        dest.writeString(inferredLocation);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType() {
        this.type = MithrilApplication.getPrefKeyContextTypeLocation();
    }

    @Override
    public String getLabel() {
        return inferredLocation;
    }

    @Override
    public void setLabel(String label) {
        inferredLocation = label;
    }
}