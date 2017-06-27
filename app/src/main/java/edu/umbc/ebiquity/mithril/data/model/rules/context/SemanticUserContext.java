package edu.umbc.ebiquity.mithril.data.model.rules.context;

import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.ContextImplementationMissingException;

public class SemanticUserContext {
    public String getType() throws ContextImplementationMissingException {
        throw new ContextImplementationMissingException("Sublcass implementation missing");
    }

    public String getLabel() throws ContextImplementationMissingException {
        throw new ContextImplementationMissingException("Sublcass implementation missing");
    }

    public void setLabel(String label) throws ContextImplementationMissingException {
        throw new ContextImplementationMissingException("Sublcass implementation missing");
    }

    public boolean isEnabled() throws ContextImplementationMissingException {
        throw new ContextImplementationMissingException("Sublcass implementation missing");
    }

    public void setEnabled(boolean enabled) throws ContextImplementationMissingException {
        throw new ContextImplementationMissingException("Sublcass implementation missing");
    }

    public int getLevel() throws ContextImplementationMissingException {
        throw new ContextImplementationMissingException("Sublcass implementation missing");
    }

    public void setLevel(int level) throws ContextImplementationMissingException {
        throw new ContextImplementationMissingException("Sublcass implementation missing");
    }
}