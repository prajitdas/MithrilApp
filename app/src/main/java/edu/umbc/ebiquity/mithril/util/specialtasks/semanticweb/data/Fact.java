package edu.umbc.ebiquity.mithril.util.specialtasks.semanticweb.data;

import com.google.gson.annotations.SerializedName;

public class Fact {

    @SerializedName("subject")
    public String subject;

    @SerializedName("predicate")
    public String predicate;

    @SerializedName("object")
    public String object;

    @Override
    public String toString() {
        return "subject: " + subject + ", predicate: " + predicate + ", object: " + object;
    }
}