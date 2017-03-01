package edu.umbc.ebiquity.mithril.util.specialtasks.detect.context;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Address;
import android.os.Build;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by prajit on 12/1/16.
 */

public class UserLocation extends Address implements Parcelable {
    private Context context;
    private Locale mLocale;

    private String mBldgRoom;
    private String mBldgFloor;
    private String mBldgNumber;
    private String mStreet;
    private String mCity;
    private String mCounty;
    private String mState;
    private String mCountry;

    @TargetApi(Build.VERSION_CODES.N)
    public UserLocation(Context context, Address address) {
        super(context.getResources().getConfiguration().getLocales().get(0));
        setContext(context);
        setBldgNumber(address.getFeatureName());
        setStreet(address.getThoroughfare());
        setCity(address.getLocality());
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        context = context;
    }

    public Locale getLocale() {
        return mLocale;
    }

    public void setLocale(Locale locale) {
        mLocale = locale;
    }

    public String getBldgRoom() {
        return mBldgRoom;
    }

    public void setBldgRoom(String bldgRoom) {
        mBldgRoom = bldgRoom;
    }

    public String getBldgFloor() {
        return mBldgFloor;
    }

    public void setBldgFloor(String bldgFloor) {
        mBldgFloor = bldgFloor;
    }

    public String getBldgNumber() {
        return mBldgNumber;
    }

    public void setBldgNumber(String bldgNumber) {
        mBldgNumber = bldgNumber;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getCounty() {
        return mCounty;
    }

    public void setCounty(String county) {
        mCounty = county;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }
}