package edu.umbc.ebiquity.mithril.data.model.rules.requesters;

import edu.umbc.ebiquity.mithril.MithrilApplication;

/**
 * The requester should be an object of the ApplicationInfo class
 *
 * @author Prajit Kumar Das
 */
public class Requester {
    private boolean adLibraryUsed; // This information should either come from PrivacyGrade or it should be extracted
    private int id;
    private String requesterCategory; // app_category: 54 Google Play Store Categories
    private String requesterContentRating; // content_rating
    private String requesterCreatorName; // developer_name
    private String requesterName; // app_name
    private String requesterPrivilegeLevel; // privilege_level: /data/app/ /system/app/ /system/priv-app/ /system/framework/ /vendor/app/
    private String requesterReviewCount; // review_count
    private String requesterReviewRating; // review_rating
    private String requesterUsageCount;    // installs

    public Requester() {
        this.id = -1;
        this.requesterName = MithrilApplication.getContextArrayRequesterCategory()[1];
    }

    public Requester(int id, String requesterName, String requesterCategory, String requesterPrivilegeLevel,
                     String requesterCreatorName, String requesterUsageCount, String requesterContentRating,
                     String requesterReviewRating, String requesterReviewCount, boolean adLibraryUsed) {
        super();
        this.id = id;
        this.requesterName = requesterName;
        this.requesterCategory = requesterCategory;
        this.requesterPrivilegeLevel = requesterPrivilegeLevel;
        this.requesterCreatorName = requesterCreatorName;
        this.requesterUsageCount = requesterUsageCount;
        this.requesterContentRating = requesterContentRating;
        this.requesterReviewRating = requesterReviewRating;
        this.requesterReviewCount = requesterReviewCount;
        this.adLibraryUsed = adLibraryUsed;
    }

    public Requester(String requesterName) {
        this.id = -1;
        this.requesterName = requesterName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Requester other = (Requester) obj;
        if (adLibraryUsed != other.adLibraryUsed)
            return false;
        if (id != other.id)
            return false;
        if (requesterCategory == null) {
            if (other.requesterCategory != null)
                return false;
        } else if (!requesterCategory.equals(other.requesterCategory))
            return false;
        if (requesterContentRating == null) {
            if (other.requesterContentRating != null)
                return false;
        } else if (!requesterContentRating.equals(other.requesterContentRating))
            return false;
        if (requesterCreatorName == null) {
            if (other.requesterCreatorName != null)
                return false;
        } else if (!requesterCreatorName.equals(other.requesterCreatorName))
            return false;
        if (requesterName == null) {
            if (other.requesterName != null)
                return false;
        } else if (!requesterName.equals(other.requesterName))
            return false;
        if (requesterPrivilegeLevel == null) {
            if (other.requesterPrivilegeLevel != null)
                return false;
        } else if (!requesterPrivilegeLevel.equals(other.requesterPrivilegeLevel))
            return false;
        if (requesterReviewCount == null) {
            if (other.requesterReviewCount != null)
                return false;
        } else if (!requesterReviewCount.equals(other.requesterReviewCount))
            return false;
        if (requesterReviewRating == null) {
            if (other.requesterReviewRating != null)
                return false;
        } else if (!requesterReviewRating.equals(other.requesterReviewRating))
            return false;
        if (requesterUsageCount == null) {
            if (other.requesterUsageCount != null)
                return false;
        } else if (!requesterUsageCount.equals(other.requesterUsageCount))
            return false;
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequesterCategory() {
        return requesterCategory;
    }

    public void setRequesterCategory(String requesterCategory) {
        this.requesterCategory = requesterCategory;
    }

    public String getRequesterContentRating() {
        return requesterContentRating;
    }

    public void setRequesterContentRating(String requesterContentRating) {
        this.requesterContentRating = requesterContentRating;
    }

    public String getRequesterCreatorName() {
        return requesterCreatorName;
    }

    public void setRequesterCreatorName(String requesterCreatorName) {
        this.requesterCreatorName = requesterCreatorName;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterPrivilegeLevel() {
        return requesterPrivilegeLevel;
    }

    public void setRequesterPrivilegeLevel(String requesterPrivilegeLevel) {
        this.requesterPrivilegeLevel = requesterPrivilegeLevel;
    }

    public String getRequesterReviewCount() {
        return requesterReviewCount;
    }

    public void setRequesterReviewCount(String requesterReviewCount) {
        this.requesterReviewCount = requesterReviewCount;
    }

    public String getRequesterReviewRating() {
        return requesterReviewRating;
    }

    public void setRequesterReviewRating(String requesterReviewRating) {
        this.requesterReviewRating = requesterReviewRating;
    }

    public String getRequesterUsageCount() {
        return requesterUsageCount;
    }

    public void setRequesterUsageCount(String requesterUsageCount) {
        this.requesterUsageCount = requesterUsageCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (adLibraryUsed ? 1231 : 1237);
        result = prime * result + id;
        result = prime * result + ((requesterCategory == null) ? 0 : requesterCategory.hashCode());
        result = prime * result + ((requesterContentRating == null) ? 0 : requesterContentRating.hashCode());
        result = prime * result + ((requesterCreatorName == null) ? 0 : requesterCreatorName.hashCode());
        result = prime * result + ((requesterName == null) ? 0 : requesterName.hashCode());
        result = prime * result + ((requesterPrivilegeLevel == null) ? 0 : requesterPrivilegeLevel.hashCode());
        result = prime * result + ((requesterReviewCount == null) ? 0 : requesterReviewCount.hashCode());
        result = prime * result + ((requesterReviewRating == null) ? 0 : requesterReviewRating.hashCode());
        result = prime * result + ((requesterUsageCount == null) ? 0 : requesterUsageCount.hashCode());
        return result;
    }

    public boolean isAdLibraryUsed() {
        return adLibraryUsed;
    }

    public void setAdLibraryUsed(boolean adLibraryUsed) {
        this.adLibraryUsed = adLibraryUsed;
    }

    @Override
    public String toString() {
        return "Requester [id=" + id + ", requesterName=" + requesterName + ", requesterCategory=" + requesterCategory
                + ", requesterPrivilegeLevel=" + requesterPrivilegeLevel + ", requesterCreatorName="
                + requesterCreatorName + ", requesterUsageCount=" + requesterUsageCount + ", requesterContentRating="
                + requesterContentRating + ", requesterReviewRating=" + requesterReviewRating
                + ", requesterReviewCount=" + requesterReviewCount + ", adLibraryUsed=" + adLibraryUsed + "]";
    }
}