package edu.umbc.ebiquity.mithril.data.model;

import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;

public class Policy implements Parcelable {
    private List<PolicyRule> policyRules;
    private int id;

    public Policy() {
        policyRules = new ArrayList<>();
    }

    protected Policy(Parcel in) {
        policyRules = in.createTypedArrayList(PolicyRule.CREATOR);
        id = in.readInt();
    }

    public static final Creator<Policy> CREATOR = new Creator<Policy>() {
        @Override
        public Policy createFromParcel(Parcel in) {
            return new Policy(in);
        }

        @Override
        public Policy[] newArray(int size) {
            return new Policy[size];
        }
    };

    public void addActionToPolicy(PolicyRule aUserAction) {
        policyRules.add(aUserAction);
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        Policy other = (Policy) obj;
        if (policyRules == null) {
            if (other.policyRules != null) {
                return false;
            }
        } else if (!policyRules.equals(other.policyRules)) {
            return false;
        }
        return true;
    }

    /**
     * @return the policyRules
     */
    public List<PolicyRule> getPolicyRules() {
        return policyRules;
    }

    /**
     * @param policyRules the policyRules to set
     */
    public void setPolicyRules(ArrayList<PolicyRule> policyRules) {
        this.policyRules = policyRules;
    }

    public String getPolicyString() {
        StringBuffer policyString = new StringBuffer();
        policyString.append("For ");
        policyString.append(policyRules.get(0).getAppStr());
        policyString.append(" access to ");
        policyString.append(policyRules.get(0).getOpStr());
        policyString.append(" allowed when context is ");
        for (PolicyRule aPolicyRule : policyRules) {
            policyString.append(aPolicyRule.getCtxStr());
            policyString.append(", ");
        }
        return policyString.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((policyRules == null) ? 0 : policyRules.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Policy [policyRules=" + policyRules + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(policyRules);
        dest.writeInt(id);
    }
}