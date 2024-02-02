package com.example.services.gedcom;

class GedcomHelper {
    private int CURRENT_PERSON_TAG = 0;

    public int getNewPersonTag() {
        return ++CURRENT_PERSON_TAG;
    }
}
