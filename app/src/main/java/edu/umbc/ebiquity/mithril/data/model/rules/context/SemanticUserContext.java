package edu.umbc.ebiquity.mithril.data.model.rules.context;

public interface SemanticUserContext {
    String getType();
    String getLabel();
    boolean isEnabled();
    void setEnabled(boolean enabled);
    void setLabel(String label);
}