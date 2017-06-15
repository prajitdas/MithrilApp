package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.location.Address;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

import edu.umbc.ebiquity.mithril.MithrilApplication;

public class SemanticLocation implements Parcelable, SemanticUserContext {
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
    private final String type = MithrilApplication.getPrefKeyContextTypeLocation();
    private Location location;
    private Address address = new Address(Locale.getDefault());
    private String inferredLocation;
    private boolean enabled = false;
    private String details = "default-details";

    public SemanticLocation(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        address = in.readParcelable(Address.class.getClassLoader());
        inferredLocation = in.readString();
        enabled = in.readByte() != 0;
        details = in.readString();
    }

    public SemanticLocation(String label, Location location) {
        this.inferredLocation = label;
        this.location = location;
        this.details = label;
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
    public String getType() {
        return type;
    }

    @Override
    public String getLabel() {
        return inferredLocation;
    }

    @Override
    public void setLabel(String label) {
        inferredLocation = label;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}