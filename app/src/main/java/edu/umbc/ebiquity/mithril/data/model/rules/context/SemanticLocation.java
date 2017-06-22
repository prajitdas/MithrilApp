package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.location.Address;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Locale;

import edu.umbc.ebiquity.mithril.MithrilAC;

public class SemanticLocation extends SemanticUserContext implements Parcelable {
    private final String type = MithrilAC.getPrefKeyContextTypeLocation();
    private Location location;
    private Address address = new Address(Locale.getDefault());
    private String inferredLocation;
    private boolean enabled = false;
    private String name;
    private String placeId;
    private List<Integer> placeTypes;
    private boolean geofenced = false;

    public SemanticLocation(String inferredLocation, Location location) {
        this.location = location;
        this.inferredLocation = inferredLocation;
    }

    protected SemanticLocation(Parcel in) {
        location = in.readParcelable(Location.class.getClassLoader());
        address = in.readParcelable(Address.class.getClassLoader());
        inferredLocation = in.readString();
        enabled = in.readByte() != 0;
        name = in.readString();
        placeId = in.readString();
        geofenced = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeParcelable(address, flags);
        dest.writeString(inferredLocation);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeString(name);
        dest.writeString(placeId);
        dest.writeByte((byte) (geofenced ? 1 : 0));
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

    @Override
    public void setLabel(String label) {
        inferredLocation = label;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(List<Integer> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public boolean isGeofenced() {
        return geofenced;
    }

    public void setGeofenced(boolean geofenced) {
        this.geofenced = geofenced;
    }
}