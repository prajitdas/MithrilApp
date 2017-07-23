package edu.umbc.ebiquity.mithril.util.specialtasks.semanticweb.data;

import java.util.ArrayList;

public class UserContextInformation {

    public String id = "";
    public String location = "";
    public ArrayList<Double> sfLocation = new ArrayList<Double>();
    public String activity = "";
    public ArrayList<Double> sfActivity = new ArrayList<Double>();

    public String info;

    @Override
    public String toString() {
        return "ID: " + id + ", location: " + location + ", activity: "
                + activity;
    }

    public FactCollection toFacts() {

        FactCollection fc = new FactCollection();

        fc.contextFacts = new ArrayList<ContextFact>();

        fc.contextFacts.add(locationToContextFact());
        fc.contextFacts.add(activityToContextFact());

        return fc;
    }

    public FactCollection toFacts(FactCollection fc) {

        fc.contextFacts.add(locationToContextFact());
        fc.contextFacts.add(activityToContextFact());

        return fc;
    }

    public ContextFact locationToContextFact() {
        // location
        ContextFact loc = new ContextFact();

        loc.device_name = id;
        // confidence on the user
        loc.confidence = 1.0;
        loc.facts = new ArrayList<Fact>();
        Fact floc = new Fact();
        floc.subject = id;
        floc.predicate = "hasLocation";
        floc.object = location;
        loc.facts.add(floc);
        loc.support = new ArrayList<Support>();
        for (double aux : sfLocation) {
            Support s = new Support();
            s.confidence = aux;
            s.fact = "a";
            s.source = "a";
            loc.support.add(s);
        }

        return loc;
    }

    public ContextFact activityToContextFact() {
        // activity
        ContextFact act = new ContextFact();

        act.device_name = id;
        // confidence on the user
        act.confidence = 1.0;
        act.facts = new ArrayList<Fact>();
        Fact facti = new Fact();
        facti.subject = id;
        facti.predicate = "hasActivity";
        // adding the individual thing!!
        facti.object = activity + "Ind";
        act.facts.add(facti);
        act.support = new ArrayList<Support>();
        for (double aux : sfLocation) {
            Support s = new Support();
            s.confidence = aux;
            s.fact = "a";
            s.source = "a";
            act.support.add(s);
        }

        return act;
    }
}