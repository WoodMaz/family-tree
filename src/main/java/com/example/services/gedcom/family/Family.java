package com.example.services.gedcom.family;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class Family {
    private final int familyTag;
    private Member wife;
    private Member husband;
    private final Set<Member> children = new HashSet<>();

    Family(int familyTag) {
        this.familyTag = familyTag;
    }

    void setWife(Member member) {
        if (this.wife != null)
            return;

        this.wife = member;
    }

    void setHusband(Member member) {
        if (this.husband != null)
            return;

        this.husband = member;
    }

    void addChild(String id, int tag) {
        this.children.add(new Member(id, tag));
    }

    public boolean isSpouse(String id) {
        if (husband == null)
            return wife.id.equals(id);
        else if (wife == null)
            return husband.id.equals(id);
        else
            return husband.id.equals(id) || wife.id.equals(id);
    }

    public boolean areSpouses(String id1, String id2) {
        if (id1 == null)
            return isSpouse(id2);
        else if (id2 == null)
            return isSpouse(id1);
        else if (id1.equals(id2))
            return false;
        else
            return isSpouse(id1) && isSpouse(id2);
    }

    public boolean isChild(String id) {
        return children.stream()
                .anyMatch(child -> child.getId().equals(id));
    }


    @Getter
    public static class Member {
        private final String id;
        private Integer tag;

        Member(String id) {
            this.id = id;
        }

        Member(String id, int tag) {
            this.id = id;
            this.tag = tag;
        }

        void setTag(int tag) {
            this.tag = tag;
        }
    }
}
