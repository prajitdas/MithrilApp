package edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces;

import java.util.ArrayList;
import java.util.List;

public class SemanticNearActors {
    private List<SemanticIdentity> listOfUsersInTheVicinity;

    public String getInferredActorRelationship() {
        return inferredActorRelationship;
    }

    public void setInferredActorRelationship(String inferredActorRelationship) {
        this.inferredActorRelationship = inferredActorRelationship;
    }

    private String inferredActorRelationship;

    public SemanticNearActors(List<SemanticIdentity> aListOfUsersInTheVicinity) {
        listOfUsersInTheVicinity = new ArrayList<>();
        setListOfUsersInTheVicinity(aListOfUsersInTheVicinity);
    }

    public SemanticNearActors(String actorsAsString) {
        listOfUsersInTheVicinity = new ArrayList<>();
        for (String actor : actorsAsString.split(","))
            listOfUsersInTheVicinity.add(new SemanticIdentity(actor));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SemanticNearActors)) {
            return false;
        }
        SemanticNearActors other = (SemanticNearActors) obj;
        if (listOfUsersInTheVicinity == null) {
            if (other.listOfUsersInTheVicinity != null) {
                return false;
            }
        } else if (!listOfUsersInTheVicinity
                .equals(other.listOfUsersInTheVicinity)) {
            return false;
        }
        return true;
    }

    /**
     * @return the listOfUsersInTheVicinity
     */
    public List<SemanticIdentity> getListOfUsersInTheVicinity() {
        return listOfUsersInTheVicinity;
    }

    /**
     * @param listOfUsersInTheVicinity the listOfUsersInTheVicinity to set
     */
    public void setListOfUsersInTheVicinity(
            List<SemanticIdentity> listOfUsersInTheVicinity) {
        this.listOfUsersInTheVicinity = listOfUsersInTheVicinity;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((listOfUsersInTheVicinity == null) ? 0
                : listOfUsersInTheVicinity.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (SemanticIdentity anSemanticIdentity : listOfUsersInTheVicinity)
            result.append(anSemanticIdentity.toString());
        return result.toString();
    }
}