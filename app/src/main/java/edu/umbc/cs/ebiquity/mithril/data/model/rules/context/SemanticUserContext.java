package edu.umbc.cs.ebiquity.mithril.data.model.rules.context;

import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticActivity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticIdentity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActors;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticTime;

public class SemanticUserContext {
    private int id;
    private SemanticNearActors semanticNearActors;
    private SemanticActivity activity;
    private SemanticIdentity semanticIdentity;
    private SemanticLocation location;
    private SemanticTime time;

    public SemanticUserContext(int id, SemanticNearActors semanticNearActors,
                               SemanticActivity activity, SemanticIdentity semanticIdentity,
                               SemanticLocation location, SemanticTime time) {
        this.id = id;
        this.semanticNearActors = semanticNearActors;
        this.activity = activity;
        this.semanticIdentity = semanticIdentity;
        this.location = location;
        this.time = time;
    }

    public SemanticUserContext(SemanticNearActors semanticNearActors,
                               SemanticActivity activity, SemanticIdentity semanticIdentity,
                               SemanticLocation location, SemanticTime time) {
        this.id = -1;
        this.semanticNearActors = semanticNearActors;
        this.activity = activity;
        this.semanticIdentity = semanticIdentity;
        this.location = location;
        this.time = time;
    }

    public SemanticUserContext() {
        this.id = -1;
        this.semanticNearActors = new SemanticNearActors();
        this.activity = new SemanticActivity();
        this.semanticIdentity = new SemanticIdentity();
        this.location = new SemanticLocation();
        this.time = new SemanticTime();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SemanticUserContext other = (SemanticUserContext) obj;
        if (activity == null) {
            if (other.activity != null)
                return false;
        } else if (!activity.equals(other.activity))
            return false;
        if (id != other.id)
            return false;
        if (semanticIdentity == null) {
            if (other.semanticIdentity != null)
                return false;
        } else if (!semanticIdentity.equals(other.semanticIdentity))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (semanticNearActors == null) {
            if (other.semanticNearActors != null)
                return false;
        } else if (!semanticNearActors.equals(other.semanticNearActors))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        return true;
    }

    public SemanticActivity getActivity() {
        return activity;
    }

    public void setActivity(SemanticActivity activity) {
        this.activity = activity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SemanticIdentity getSemanticIdentity() {
        return semanticIdentity;
    }

    public void setSemanticIdentity(SemanticIdentity semanticIdentity) {
        this.semanticIdentity = semanticIdentity;
    }

    public SemanticLocation getLocation() {
        return location;
    }

    public void setLocation(SemanticLocation location) {
        this.location = location;
    }

    public SemanticNearActors getSemanticNearActors() {
        return semanticNearActors;
    }

    public void setSemanticNearActors(SemanticNearActors semanticNearActors) {
        this.semanticNearActors = semanticNearActors;
    }

    public SemanticTime getTime() {
        return time;
    }

    public void setTime(SemanticTime time) {
        this.time = time;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((activity == null) ? 0 : activity.hashCode());
        result = prime * result + id;
        result = prime * result
                + ((semanticIdentity == null) ? 0 : semanticIdentity.hashCode());
        result = prime * result
                + ((location == null) ? 0 : location.hashCode());
        result = prime * result
                + ((semanticNearActors == null) ? 0 : semanticNearActors.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "SemanticUserContext [id=" + id + ", semanticNearActors=" + semanticNearActors
                + ", activity=" + activity + ", semanticIdentity=" + semanticIdentity
                + ", location=" + location + ", time=" + time + "]";
    }
}