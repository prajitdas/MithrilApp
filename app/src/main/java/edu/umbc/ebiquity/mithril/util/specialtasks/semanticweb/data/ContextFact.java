package edu.umbc.ebiquity.mithril.util.specialtasks.semanticweb.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContextFact {

	@SerializedName("_id")
	public String _id;

	@SerializedName("device_name")
	public String device_name;

	@SerializedName("confidence")
	public double confidence;

	public List<Fact> facts;

	public List<Support> support;

	@SerializedName("source")
	public String source;

	@Override
	public String toString() {

		String text = "_id: " + _id + ", device_name: " + device_name + ", confidence: " + confidence + ", source: "
				+ source;

		text += ", fact: ";
		for (Fact aux : facts)
			text += "{"+ aux.toString()+"}";
		text += ", support: ";
		for (Support aux : support)
			text += "{" + aux.toString()+"}";

		return text;
	}

}