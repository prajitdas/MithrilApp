package edu.umbc.cs.ebiquity.mithril.data.model.rules.context;

import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticActivity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticIdentity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActors;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticTime;

public class SemanticUserContext {
    private int id;
    private SemanticNearActors semanticNearActors;
    private SemanticActivity semanticActivity;
    private SemanticIdentity semanticIdentity;
    private SemanticLocation semanticLocation;
    private SemanticTime semanticTime;

    public SemanticUserContext() {
        semanticNearActors = new SemanticNearActors();
        semanticActivity = new SemanticActivity();
        semanticIdentity = new SemanticIdentity();
        semanticLocation = new SemanticLocation();
        semanticTime = new SemanticTime();
    }

    public SemanticUserContext(int id, SemanticNearActors semanticNearActors,
                               SemanticActivity semanticActivity, SemanticIdentity semanticIdentity,
                               SemanticLocation semanticLocation, SemanticTime semanticTime) {
        this.id = id;
        this.semanticNearActors = semanticNearActors;
        this.semanticActivity = semanticActivity;
        this.semanticIdentity = semanticIdentity;
        this.semanticLocation = semanticLocation;
        this.semanticTime = semanticTime;
    }

    public SemanticUserContext(SemanticNearActors semanticNearActors,
                               SemanticActivity semanticActivity, SemanticIdentity semanticIdentity,
                               SemanticLocation semanticLocation, SemanticTime semanticTime) {
        this.id = -1;
        this.semanticNearActors = semanticNearActors;
        this.semanticActivity = semanticActivity;
        this.semanticIdentity = semanticIdentity;
        this.semanticLocation = semanticLocation;
        this.semanticTime = semanticTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SemanticNearActors getSemanticNearActors() {
        return semanticNearActors;
    }

    public void setSemanticNearActors(SemanticNearActors semanticNearActors) {
        this.semanticNearActors = semanticNearActors;
    }

    public SemanticActivity getSemanticActivity() {
        return semanticActivity;
    }

    public void setSemanticActivity(SemanticActivity semanticActivity) {
        this.semanticActivity = semanticActivity;
    }

    public SemanticIdentity getSemanticIdentity() {
        return semanticIdentity;
    }

    public void setSemanticIdentity(SemanticIdentity semanticIdentity) {
        this.semanticIdentity = semanticIdentity;
    }

    public SemanticLocation getSemanticLocation() {
        return semanticLocation;
    }

    public void setSemanticLocation(SemanticLocation semanticLocation) {
        this.semanticLocation = semanticLocation;
    }

    public SemanticTime getSemanticTime() {
        return semanticTime;
    }

    public void setSemanticTime(SemanticTime semanticTime) {
        this.semanticTime = semanticTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticUserContext)) return false;

        SemanticUserContext that = (SemanticUserContext) o;

        if (getId() != that.getId()) return false;
        if (!getSemanticNearActors().equals(that.getSemanticNearActors())) return false;
        if (!getSemanticActivity().equals(that.getSemanticActivity())) return false;
        if (!getSemanticIdentity().equals(that.getSemanticIdentity())) return false;
        if (!getSemanticLocation().equals(that.getSemanticLocation())) return false;
        return getSemanticTime().equals(that.getSemanticTime());

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getSemanticNearActors().hashCode();
        result = 31 * result + getSemanticActivity().hashCode();
        result = 31 * result + getSemanticIdentity().hashCode();
        result = 31 * result + getSemanticLocation().hashCode();
        result = 31 * result + getSemanticTime().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SemanticUserContext{" +
                "id=" + id +
                ", semanticNearActors=" + semanticNearActors +
                ", semanticActivity=" + semanticActivity +
                ", semanticIdentity=" + semanticIdentity +
                ", semanticLocation=" + semanticLocation +
                ", semanticTime=" + semanticTime +
                '}';
    }
}