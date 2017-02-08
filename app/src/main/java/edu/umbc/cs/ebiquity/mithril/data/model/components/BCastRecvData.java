package edu.umbc.cs.ebiquity.mithril.data.model.components;

import android.support.annotation.NonNull;

/**
 * Created by Prajit on 1/25/2017.
 */

public class BCastRecvData implements Comparable<BCastRecvData> {
    private String name;
    private String label;

    @Override
    public int compareTo(@NonNull BCastRecvData o) {
        return 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BCastRecvData)) return false;

        BCastRecvData that = (BCastRecvData) o;

        if (!getName().equals(that.getName())) return false;
        return getLabel().equals(that.getLabel());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getLabel().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "BCastRecvData{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}