package edu.umbc.ebiquity.mithril.util.specialtasks.semanticweb.data;

import java.util.ArrayList;

public class UserContextInformationForActivities {

    private String location;
    private String activity;
    private String identity;
    private ArrayList<Double> listOfSupportingFactsLocation;
    private ArrayList<Double> listOfSupportingFactsActivity;

    public UserContextInformationForActivities() {
        listOfSupportingFactsLocation = new ArrayList<Double>();
        listOfSupportingFactsActivity = new ArrayList<Double>();
    }

    @Override
    public String toString() {
        return "ID: " + this.getIdentity() + ", location: " + this.getLocation() + ", activity: " + this.getActivity();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public ArrayList<Double> getListOfSupportingFactsLocation() {
        return listOfSupportingFactsLocation;
    }

    public void setListOfSupportingFactsLocation(
            ArrayList<Double> listOfSupportingFactsLocation) {
        this.listOfSupportingFactsLocation.addAll(listOfSupportingFactsLocation);
    }

    public ArrayList<Double> getListOfSupportingFactsActivity() {
        return listOfSupportingFactsActivity;
    }

    public void setListOfSupportingFactsActivity(
            ArrayList<Double> listOfSupportingFactsActivity) {
        this.listOfSupportingFactsActivity.addAll(listOfSupportingFactsActivity);
    }
}