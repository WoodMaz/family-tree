package com.example.services.gedcom;

import com.example.enums.Sex;
import com.example.models.Person;
import com.example.utils.CommonUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class GedcomService {
    private static final String PERSON_TAG_PATTERN = "0 @I%d@ INDI\n";
    private static final String NAME_PATTERN = "1 NAME %s";
    private static final String SURNAME_PATTERN = " /%s/\n";
    private static final String SEX_PATTERN = "1 SEX %s\n";
    private static final String BIRTH_DATE_PATTERN = "1 BIRT\n2 DATE %s\n";
    private static final String DEATH_DATE_PATTERN = "1 DEAT\n2 DATE %s\n";
    private static final String PARENT_PATTERN = "1 FAMS @F%d@\n";
    private static final String CHILD_PATTERN = "1 FAMC @F%d@\n";

    private static final String FAM_TAG_PATTERN = "0 @F%d@ FAM\n";
    private static final String FAM_HUSBAND_PATTERN = "1 HUSB @I%d@\n";
    private static final String FAM_WIFE_PATTERN = "1 WIFE @I%d@\n";
    private static final String FAM_CHILD_PATTERN = "1 CHIL @I%d@\n";

    private static final String DATE_FORMATTER = "d MMM yyyy";


    public String createGedcom(List<Person> persons, String username) {
        StringBuilder output = new StringBuilder();
        writeHeaders(output);

        GedcomHelper gedcomHelper = new GedcomHelper();
        List<FamHelper> famHelpers = new ArrayList<>();
        persons.forEach(p -> addPerson(output, p, famHelpers, gedcomHelper));

        writeRelations(output, famHelpers);
        writeEnding(output, username);

        return output.toString();
    }

    private void writeHeaders(StringBuilder output) {
        output.append("0 HEAD\n");
        output.append("1 CHAR UTF-8\n");
        output.append("1 SOUR WoodMaz\n");
        output.append("1 GEDC\n");
        output.append("2 VERS 5.5.5\n");
        output.append("2 FORM LINEAGE-LINKED\n");
    }

    private void writeEnding(StringBuilder output, String username) {
        output.append("0 @SUBM@ SUBM\n");
        output.append(String.format("1 NAME %s\n", username));
        output.append("0 TRLR");
    }

    private void writeRelations(StringBuilder output, List<FamHelper> famHelpers) {
        for(FamHelper fam : famHelpers) {
            output.append(String.format(FAM_TAG_PATTERN, fam.getFamNo()));

            FamHelper.FamPerson wife = fam.getWife();
            FamHelper.FamPerson husband = fam.getHusband();
            Set<FamHelper.FamPerson> children = fam.getChildren();

            if (wife != null)
                output.append(String.format(FAM_WIFE_PATTERN, wife.getTag()));

            if (husband != null)
                output.append(String.format(FAM_HUSBAND_PATTERN, husband.getTag()));

            children.forEach(child -> output.append(String.format(FAM_CHILD_PATTERN, child.getTag())));
        }
    }

    private void addPerson(StringBuilder output, Person person, List<FamHelper> famHelpers, GedcomHelper gedcomHelper) {
        int personTag = gedcomHelper.getNewPersonTag();

        if (famHelpers.isEmpty()) {
            famHelpers.addAll(createFamsIfNecessary(person, personTag));
            writePersonData(person, personTag, output, famHelpers);
            return;
        }

        //osoba jako dziecko
        for (FamHelper fam : famHelpers) {
            if (areParentsCheck(fam, person)) {
                fam.addChild(person.getId(), personTag);
                break;
            }

            //jeżeli nie ma fama, w którym person jest dzieckiem, tworzymy nowego
            createFamWithChild(person, personTag)
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

        writePersonData(person, personTag, output, famHelpers);
    }

    private void writePersonData(Person person, int personTag, StringBuilder output, List<FamHelper> famHelpers) {
        output.append(String.format(PERSON_TAG_PATTERN, personTag));

        String name = person.getName();
        String surname = person.getSurname();
        Sex sex = person.getSex();
        LocalDate birthDate = person.getBirthDate();
        LocalDate deathDate = person.getDeathDate();

        if (CommonUtils.hasContent(name))
            output.append(String.format(NAME_PATTERN, person.getName()));

        if (CommonUtils.hasContent(surname))
            output.append(String.format(SURNAME_PATTERN, person.getSurname()));

        if (sex != null)
            output.append(String.format(SEX_PATTERN, sex.getGedcomFormat()));

        if (birthDate != null)
            output.append(String.format(BIRTH_DATE_PATTERN, birthDate.format(DateTimeFormatter.ofPattern(DATE_FORMATTER))));

        if (deathDate != null)
            output.append(String.format(DEATH_DATE_PATTERN, deathDate.format(DateTimeFormatter.ofPattern(DATE_FORMATTER))));

        for (FamHelper fam : famHelpers) {
            if (fam.isParent(person.getId())) {
                output.append(String.format(PARENT_PATTERN, fam.getFamNo()));
            } else if (fam.isChild(person.getId())) {
                output.append(String.format(CHILD_PATTERN, fam.getFamNo()));
            }
        }
    }

    //ta metoda tylko tworzy famHelpery jeżeli person ma rodzica/ów, lub małżonka/ów.
    // Sprawdzanie, czy w famHelpers jest już fam z tym personem nie jest tu obsługiwane
    private List<FamHelper> createFamsIfNecessary(Person person, int personTag) {
        List<FamHelper> fams = new ArrayList<>(createFamsForAllSpouses(person, personTag));

        createFamWithChild(person, personTag)
                .ifPresent(fams::add);

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

    private Optional<FamHelper> createFamWithChild(Person child, int childTag) {
        if (child.getMotherId() != null || child.getFatherId() != null) {
            return Optional.of(new FamHelper(child.getId(), childTag, child.getMotherId(), child.getFatherId()));
        }

        return Optional.empty();
    }

    private boolean areParentsCheck(FamHelper fam, Person person) {
        return (fam.getWife().getId().equals(person.getMotherId()))
                && (fam.getHusband().getId().equals(person.getFatherId()));
    }
}


