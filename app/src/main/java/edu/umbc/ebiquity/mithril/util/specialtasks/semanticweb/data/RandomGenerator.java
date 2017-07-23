package edu.umbc.ebiquity.mithril.util.specialtasks.semanticweb.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

public class RandomGenerator {

    public static String[] names = {"Jeff", "Abed", "Annie", "Pierce"};
    public static String[] activities = {"StudyGroup", "Reading"};
    public static String[] locations = {"USA", "GCC", "Library", "StudyRoomF", "Home"};

    public static int maxRandomFacts = 4;

    public static String generateJson(int numUser) {

        Random ran = new Random();
        FactCollection fc = new FactCollection();
        fc.contextFacts = new ArrayList<ContextFact>();

        for (int i = 0; i < numUser; i++) {
            //generating random number of facts for each user
            int numFacts = ran.nextInt(maxRandomFacts) + 1;

            for (int j = 0; j < numFacts; j++) {
                UserContextInformation aux = new UserContextInformation();

                aux.id = names[ran.nextInt(names.length)];
                aux.activity = activities[ran.nextInt(activities.length)];
                aux.sfActivity = new ArrayList<Double>();
                aux.sfActivity.add(ran.nextInt(10) / 10.0);
                aux.location = locations[ran.nextInt(locations.length)];
                aux.sfLocation = new ArrayList<Double>();
                aux.sfLocation.add(ran.nextInt(10) / 10.0);

                aux.toFacts(fc);
            }
        }

        Gson gson = new Gson();

        return gson.toJson(fc);
    }
}
