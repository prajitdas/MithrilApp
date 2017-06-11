package edu.umbc.ebiquity.mithril.data.model.rules.context;

public interface SemanticUserContext {
    String getType();
    String getLabel();
    void setLabel(String label);
}