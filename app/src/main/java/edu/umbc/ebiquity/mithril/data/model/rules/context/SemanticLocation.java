package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.location.Address;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;

import java.util.Locale;

import edu.umbc.ebiquity.mithril.MithrilAC;

public class SemanticLocation extends SemanticUserContext implements Parcelable {
    private final String type = MithrilAC.getPrefKeyContextTypeLocation();
    private Location location;
    private Address address = new Address(Locale.getDefault());
    private String inferredLocation;
    private boolean enabled = false;
    private String details = "default-details";
    private Place place;

    protected SemanticLocation(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        address = in.readParcelable(Address.class.getClassLoader());
        inferredLocation = in.readString();
        enabled = in.readByte() != 0;
        details = in.readString();
    }

    public SemanticLocation(String inferredLocation, Location location) {
        this.location = location;
        this.inferredLocation = inferredLocation;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeParcelable(address, flags);
        dest.writeString(inferredLocation);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeString(details);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SemanticLocation> CREATOR = new Creator<SemanticLocation>() {
        @Override
        public SemanticLocation createFromParcel(Parcel in) {
            return new SemanticLocation(in);
        }

        @Override
        public SemanticLocation[] newArray(int size) {
            return new SemanticLocation[size];
        }
    };

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getLabel() {
        return inferredLocation;
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

    public String getInferredLocation() {
        return inferredLocation;
    }

    public void setInferredLocation(String inferredLocation) {
        this.inferredLocation = inferredLocation;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setLabel(String label) {
        inferredLocation = label;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticLocation)) return false;

        SemanticLocation that = (SemanticLocation) o;

        if (isEnabled() != that.isEnabled()) return false;
        if (!getType().equals(that.getType())) return false;
        if (!getLocation().equals(that.getLocation())) return false;
        if (!getAddress().equals(that.getAddress())) return false;
        if (!getInferredLocation().equals(that.getInferredLocation())) return false;
        if (!getDetails().equals(that.getDetails())) return false;
        return getPlace().equals(that.getPlace());
    }

    @Override
    public int hashCode() {
        int result = getType().hashCode();
        result = 31 * result + getLocation().hashCode();
        result = 31 * result + getAddress().hashCode();
        result = 31 * result + getInferredLocation().hashCode();
        result = 31 * result + (isEnabled() ? 1 : 0);
        result = 31 * result + getDetails().hashCode();
        result = 31 * result + getPlace().hashCode();
        return result;
    }
}