package com.example.services.gedcom;

class GedcomCounter {
    private int CURRENT_PERSON_TAG = 0;
    private int CURRENT_FAMILY_TAG = 0;

    public int getNewPersonTag() {
        return ++CURRENT_PERSON_TAG;
    }

    public int getNewFamilyTag() {
        return ++CURRENT_FAMILY_TAG;
    }
}
