package com.example.services.gedcom.family;

public abstract class FamilyHelper {
    public Family createFamilyForChild(int familyTag, String childId, int childTag, String motherId, String fatherId) {
        Family family = new Family(familyTag);

        family.addChild(childId, childTag);

        if (motherId != null) {
            family.setWife(new Family.Member(motherId));
        }

        if (fatherId != null) {
            family.setHusband(new Family.Member(fatherId));
        }

        return family;
    }

    public void addChild(Family family, String id, int tag) {
        family.addChild(id, tag);
    }

    public abstract Family createFamilyForSpouse(int familyTag, String personId, int personTag, String spouseId);

    public abstract void setTagIfSpouse(Family family, String id, int tag);

}
