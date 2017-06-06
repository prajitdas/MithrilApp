package edu.umbc.ebiquity.mithril.data.model.rules.context;

import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticTime;

public class SemanticUserContext {
    private int id;
    private SemanticNearActor semanticNearActor;
    private SemanticActivity semanticActivity;
    private SemanticLocation semanticLocation;
    private SemanticTime semanticTime;

    public SemanticUserContext() {
        semanticNearActor = new SemanticNearActor();
        semanticActivity = new SemanticActivity();
        semanticLocation = new SemanticLocation();
        semanticTime = new SemanticTime();
    }

    public SemanticUserContext(int id, SemanticNearActor semanticNearActor, SemanticActivity semanticActivity,
                               SemanticLocation semanticLocation, SemanticTime semanticTime) {
        this.id = id;
        this.semanticNearActor = semanticNearActor;
        this.semanticActivity = semanticActivity;
        this.semanticLocation = semanticLocation;
        this.semanticTime = semanticTime;
    }

    public SemanticUserContext(SemanticNearActor semanticNearActor, SemanticActivity semanticActivity,
                               SemanticLocation semanticLocation, SemanticTime semanticTime) {
        this.id = -1;
        this.semanticNearActor = semanticNearActor;
        this.semanticActivity = semanticActivity;
        this.semanticLocation = semanticLocation;
        this.semanticTime = semanticTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SemanticNearActor getSemanticNearActor() {
        return semanticNearActor;
    }

    public void setSemanticNearActor(SemanticNearActor semanticNearActor) {
        this.semanticNearActor = semanticNearActor;
    }

    public SemanticActivity getSemanticActivity() {
        return semanticActivity;
    }

    public void setSemanticActivity(SemanticActivity semanticActivity) {
        this.semanticActivity = semanticActivity;
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
        if (getSemanticNearActor() != null ? !getSemanticNearActor().equals(that.getSemanticNearActor()) : that.getSemanticNearActor() != null)
            return false;
        if (getSemanticActivity() != null ? !getSemanticActivity().equals(that.getSemanticActivity()) : that.getSemanticActivity() != null)
            return false;
        if (getSemanticLocation() != null ? !getSemanticLocation().equals(that.getSemanticLocation()) : that.getSemanticLocation() != null)
            return false;
        return getSemanticTime() != null ? getSemanticTime().equals(that.getSemanticTime()) : that.getSemanticTime() == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getSemanticNearActor() != null ? getSemanticNearActor().hashCode() : 0);
        result = 31 * result + (getSemanticActivity() != null ? getSemanticActivity().hashCode() : 0);
        result = 31 * result + (getSemanticLocation() != null ? getSemanticLocation().hashCode() : 0);
        result = 31 * result + (getSemanticTime() != null ? getSemanticTime().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SemanticUserContext{" +
                "id=" + id +
                ", semanticNearActor=" + semanticNearActor +
                ", semanticActivity=" + semanticActivity +
                ", semanticLocation=" + semanticLocation +
                ", semanticTime=" + semanticTime +
                '}';
    }
}