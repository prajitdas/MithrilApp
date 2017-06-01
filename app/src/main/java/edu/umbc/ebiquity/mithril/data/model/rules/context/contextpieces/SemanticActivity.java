package edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces;

import edu.umbc.ebiquity.mithril.MithrilApplication;

public class SemanticActivity {
    private String rawActivity;
    private String inferredActivity;

    public SemanticActivity(String rawActivity, String inferredActivity) {
        setRawActivity(rawActivity);
        setInferredActivity(inferredActivity);
    }

    public SemanticActivity(String inferredActivity) {
        setInferredActivity(inferredActivity);
        setRawActivity("N/A");
    }

    public SemanticActivity() {
        setInferredActivity(MithrilApplication.getContextDefaultActivity());
        setRawActivity("N/A");
    }

    public String getRawActivity() {
        return rawActivity;
    }

    public void setRawActivity(String rawActivity) {
        this.rawActivity = rawActivity;
    }

    public String getInferredActivity() {
        return inferredActivity;
    }

    public void setInferredActivity(String inferredActivity) {
        this.inferredActivity = inferredActivity;
    }

    @Override
    public String toString() {
        return "SemanticActivity{" +
                "rawActivity='" + rawActivity + '\'' +
                ", inferredActivity='" + inferredActivity + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticActivity)) return false;

        SemanticActivity that = (SemanticActivity) o;

        if (!getRawActivity().equals(that.getRawActivity())) return false;
        return getInferredActivity().equals(that.getInferredActivity());
    }

    @Override
    public int hashCode() {
        int result = getRawActivity().hashCode();
        result = 31 * result + getInferredActivity().hashCode();
        return result;
    }
}