package com.example.services.gedcom;

class GedcomHelper {

    private int CURRENT_PERSON_TAG = -1;

    public int getNewPersonTag() {
        return ++CURRENT_PERSON_TAG;
    }
}
