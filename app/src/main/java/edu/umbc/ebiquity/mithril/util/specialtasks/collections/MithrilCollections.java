package edu.umbc.ebiquity.mithril.util.specialtasks.collections;

import java.util.List;
import java.util.Set;

public class MithrilCollections {
    public static boolean isExactMatchList(List<?> listA, List<?> listB) {
        if (listA.size() != listB.size())
            return false;
        for(Object object : listA)
            if(!listB.contains(object))
                return false;
        return true;
    }

    public static boolean isExactMatchSet(Set<?> setA, Set<?> setB) {
        if (setA.size() != setB.size())
            return false;
        for(Object object : setA)
            if(!setB.contains(object))
                return false;
        return true;
    }

    public static boolean isSubset(Set<?> setA, Set<?> setB) {
        if (setA.size() >= setB.size())
            return false;
        for(Object object : setA)
            if(!setB.contains(object))
                return false;
        return true;
    }
}