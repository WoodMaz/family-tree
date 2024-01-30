package com.example.services.gedcom;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
class FamHelper {
    private static int FAM_COUNTER = 0;

    private final int famNo;
    private FamPerson mother;
    private FamPerson father;
    private final Set<FamPerson> children = new HashSet<>();

    public FamHelper() {
        this.famNo = getNewFamNo();
    }

    public FamHelper(String childId, int childTag, String motherId, String fatherId) {
        this.famNo = getNewFamNo();

        addChild(childId, childTag);
        this.mother = new FamPerson(motherId);
        this.father = new FamPerson(fatherId);
    }

    public FamHelper(String motherId, int motherTag, String fatherId) {
        this.famNo = getNewFamNo();

        this.mother = new FamPerson(motherId, motherTag);
        this.father = new FamPerson(fatherId);
    }

    public FamHelper(String motherId, String fatherId, int fatherTag) {
        this.famNo = getNewFamNo();

        this.mother = new FamPerson(motherId);
        this.father = new FamPerson(fatherId, fatherTag);
    }

    public void setTagIfMother(String id, int tag) {
        if (this.mother.id.equals(id)) {
            this.mother.tag = tag;
        }
    }

    public void setTagIfFather(String id, int tag) {
        if (this.father.id.equals(id)) {
            this.father.tag = tag;
        }
    }

    public void addChild(String id, int tag) {
        this.children.add(new FamPerson(id, tag));
    }

    public boolean areParents(String id1, String id2) {
        return (id1.equals(father.id) && id2.equals(mother.id))
                || (id1.equals(mother.id) && id2.equals(father.id)) ;
    }

    public void createFather(String id) {
        if (father == null) {
            father = new FamPerson(id);
        }
    }

    public void createMother(String id) {
        if (mother == null) {
            mother = new FamPerson(id);
        }
    }

    private int getNewFamNo() {
        return ++FAM_COUNTER;
    }

    @Getter
    static class FamPerson {
        private final String id;
        private int tag;

        private FamPerson(String id) {
            this.id = id;
        }

        private FamPerson(String id, int tag) {
            this.id = id;
            this.tag = tag;
        }
    }
}
