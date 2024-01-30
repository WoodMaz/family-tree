package com.example.services.gedcom;

import com.example.models.Person;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class GedcomService {
    private static final String PERSON_TAG_PATTERN = "0 @I%s@@ INDI\n";
    private static final String NAME_PATTERN = "1 NAME %s \\%s\\";
    private static final String SEX_PATTERN = "1 SEX %s";
    private static final String BIRTH_DATE_PATTERN = "1 BIRT\n2 DATE %s";
    private static final String DEATH_DATE_PATTERN = "1 DEAT\n2 DATE %s";


    private final GedcomHelper gedcomHelper;

    public String createGedcom(List<Person> persons, String username) {
        StringBuilder output = new StringBuilder();
        addHeaders(output, username);

        List<FamHelper> famHelpers = new ArrayList<>();
        persons.forEach(p -> addPerson(output, p, famHelpers));

        return output.toString();
    }

    public void addHeaders(StringBuilder sb, String username) {
        sb.append("0 HEAD\n");
        sb.append("1 CHAR UTF-8\n");
        sb.append("1 SOUR WoodMaz\n");
        sb.append("1 GEDC\n");
        sb.append("2 VERS 5.5.5\n");
        sb.append("2 FORM LINEAGE-LINKED\n");
    }

    //TODO: obsłużyć przypadki, gdy encja ma pola puste/nulle, np stworzyć instancję FamHelper.Person.UNKNOWN
    private void addPerson(StringBuilder sb, Person person, List<FamHelper> famHelpers) {
        int personTag = gedcomHelper.getNewPersonTag();
        sb.append(String.format(PERSON_TAG_PATTERN, personTag));
        sb.append(String.format(NAME_PATTERN, person.getName(), person.getSurname()));
        sb.append(String.format(SEX_PATTERN, person.getSex().toString()));
        sb.append(String.format(BIRTH_DATE_PATTERN, person.getBirthDate().toString())); //TODO: this probably should be formatted e.g. '11 Jun 1861'
        sb.append(String.format(DEATH_DATE_PATTERN, person.getBirthDate().toString()));


        if (famHelpers.isEmpty()) {
            famHelpers.addAll(createFamsIfNecessary(person, personTag));
            return;
        }

        //osoba jako dziecko
        for (FamHelper fam : famHelpers) {
            if (areParentsCheck(fam, person)) {
                fam.addChild(person.getId(), personTag);
                break;
            }

            //jeżeli nie ma fama, w którym person jest dzieckiem, tworzymy nowego
            createFamWhereChildIsGiven(person, personTag)
                    .ifPresent(famHelpers::add);
        }

        //dodawanie małżeństw
        for (String spouseId : person.getSpouseIds()) {
            boolean marriageDontExist = true;

            for (FamHelper fam : famHelpers) {
                if (fam.areParents(person.getId(), spouseId)) {
                    marriageDontExist = false;
                    break;
                }
            }

            if (marriageDontExist) {
                famHelpers.add(createFamForMarriage(person, personTag, spouseId));
            }
        }

        //TODO: może da się to jakoś połączyć z poprzednią pętlą
        switch (person.getSex()) {
            case FEMALE -> famHelpers.forEach(fam -> fam.setTagIfMother(person.getId(), personTag));
            case MALE -> famHelpers.forEach(fam -> fam.setTagIfFather(person.getId(), personTag));
        }
    }

    //ta metoda tylko tworzy famHelpery jeżeli person ma rodzica/ów, lub małżonka/ów.
    // Sprawdzanie, czy w famHelpers jest już fam z tym personem nie jest tu obsługiwane
    private List<FamHelper> createFamsIfNecessary(Person person, int personTag) {
        List<FamHelper> fams = new ArrayList<>();

        fams.addAll(createFamsForAllSpouses(person, personTag));

        createFamWhereChildIsGiven(person, personTag).ifPresent(fams::add);

        return fams;
    }

    private List<FamHelper> createFamsForAllSpouses(Person person, int personTag) {
        List<FamHelper> fams = new ArrayList<>();

        switch (person.getSex()) {
            case FEMALE -> person.getSpouseIds().forEach(spouseId -> fams.add(new FamHelper(person.getId(), personTag, spouseId)));
            case MALE -> person.getSpouseIds().forEach(spouseId -> fams.add(new FamHelper(spouseId, person.getId(), personTag)));
        }

        return fams;
    }

    private FamHelper createFamForMarriage(Person person, int personTag, String spouseId) {
        switch (person.getSex()) {
            case FEMALE -> {
                return new FamHelper(person.getId(), personTag, spouseId);
            }
            case MALE -> {
                return new FamHelper(spouseId, person.getId(), personTag);
            }
            default -> throw new RuntimeException();
        }
    }

    private Optional<FamHelper> createFamWhereChildIsGiven(Person child, int childTag) {
        if (child.getMotherId() != null || child.getFatherId() != null) {
            return Optional.of(new FamHelper(child.getId(), childTag, child.getMotherId(), child.getFatherId()));
        }

        return Optional.empty();
    }

    private boolean areParentsCheck(FamHelper fam, Person person) {
        return (fam.getMother().getId().equals(person.getMotherId()))
                && (fam.getFather().getId().equals(person.getFatherId()));
    }
}


