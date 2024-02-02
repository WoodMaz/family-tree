package com.example.services.gedcom;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
class FamHelper {
    private static int FAM_COUNTER = 0;

    private final int famNo;
    private FamPerson wife;
    private FamPerson husband;
    private final Set<FamPerson> children = new HashSet<>();

    public FamHelper() {
        this.famNo = getNewFamNo();
    }

    public FamHelper(String childId, int childTag, String motherId, String fatherId) {
        this.famNo = getNewFamNo();

        addChild(childId, childTag);
        this.wife = new FamPerson(motherId);
        this.husband = new FamPerson(fatherId);
    }

    public FamHelper(String motherId, int motherTag, String fatherId) {
        this.famNo = getNewFamNo();

        this.wife = new FamPerson(motherId, motherTag);
        this.husband = new FamPerson(fatherId);
    }

    public FamHelper(String motherId, String fatherId, int fatherTag) {
        this.famNo = getNewFamNo();

        this.wife = new FamPerson(motherId);
        this.husband = new FamPerson(fatherId, fatherTag);
    }


    public void setTagIfMother(String id, int tag) {
        if (this.wife.id.equals(id)) {
            this.wife.tag = tag;
        }
    }

    public void setTagIfFather(String id, int tag) {
        if (this.husband.id.equals(id)) {
            this.husband.tag = tag;
        }
    }

    public void addChild(String id, int tag) {
        this.children.add(new FamPerson(id, tag));
    }

    public boolean isParent(String id) {
        return id.equals(husband.id) || id.equals(wife.id);
    }

    public boolean areParents(String id1, String id2) {
        return (id1.equals(husband.id) && id2.equals(wife.id))
                || (id1.equals(wife.id) && id2.equals(husband.id));
    }

    public boolean isChild(String id) {
        return children.stream()
                .anyMatch(child -> child.getId().equals(id));
    }

    private int getNewFamNo() {
        return ++FAM_COUNTER;
    }

    @Getter
    static class FamPerson {
        private final String id;
        private Integer tag;

        private FamPerson(String id) {
            this.id = id;
        }

        private FamPerson(String id, int tag) {
            this.id = id;
            this.tag = tag;
        }
    }
}
