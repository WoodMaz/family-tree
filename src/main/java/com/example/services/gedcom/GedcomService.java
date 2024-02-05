package com.example.services.gedcom;

import com.example.enums.Sex;
import com.example.models.Person;
import com.example.services.gedcom.family.Family;
import com.example.services.gedcom.family.FamilyFemaleHelper;
import com.example.services.gedcom.family.FamilyHelper;
import com.example.services.gedcom.family.FamilyMaleHelper;
import com.example.utils.CommonUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GedcomService {
    private static final String PERSON_TAG_PATTERN = "0 @I%d@ INDI\n";
    private static final String NAME_PATTERN = "1 NAME %s";
    private static final String SURNAME_PATTERN = " /%s/\n";
    private static final String SEX_PATTERN = "1 SEX %s\n";
    private static final String BIRTH_DATE_PATTERN = "1 BIRT\n2 DATE %s\n";
    private static final String DEATH_DATE_PATTERN = "1 DEAT\n2 DATE %s\n";
    private static final String PARENT_PATTERN = "1 FAMS @F%d@\n";
    private static final String CHILD_PATTERN = "1 FAMC @F%d@\n";

    private static final String FAMILY_TAG_PATTERN = "0 @F%d@ FAM\n";
    private static final String FAMILY_HUSBAND_PATTERN = "1 HUSB @I%d@\n";
    private static final String FAMILY_WIFE_PATTERN = "1 WIFE @I%d@\n";
    private static final String FAMILY_CHILD_PATTERN = "1 CHIL @I%d@\n";

    private static final String DATE_FORMATTER = "d MMM yyyy";


    private final FamilyFemaleHelper familyFemaleHelper;
    private final FamilyMaleHelper familyMaleHelper;

    private GedcomCounter gedcomCounter;


    public GedcomService(FamilyFemaleHelper familyFemaleHelper, FamilyMaleHelper familyMaleHelper) {
        this.familyFemaleHelper = familyFemaleHelper;
        this.familyMaleHelper = familyMaleHelper;
    }


    public String createGedcom(List<Person> persons, String username) {
        StringBuilder output = new StringBuilder();
        writeHeaders(output);

        gedcomCounter = new GedcomCounter();
        List<Family> families = new ArrayList<>();
        persons.forEach(p -> addPerson(output, p, families));

        writeRelations(output, families);
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

    private void writeRelations(StringBuilder output, List<Family> families) {
        for(Family family : families) {
            output.append(String.format(FAMILY_TAG_PATTERN, family.getFamilyTag()));

            Family.Member wife = family.getWife();
            Family.Member husband = family.getHusband();

            if (wife != null)
                output.append(String.format(FAMILY_WIFE_PATTERN, wife.getTag()));

            if (husband != null)
                output.append(String.format(FAMILY_HUSBAND_PATTERN, husband.getTag()));

            family.getChildren().forEach(child -> output.append(String.format(FAMILY_CHILD_PATTERN, child.getTag())));
        }
    }

    private void addPerson(StringBuilder output, Person person, List<Family> families) {
        FamilyHelper familyHelper;
        switch (person.getSex()) {
            case FEMALE -> familyHelper = this.familyFemaleHelper;
            case MALE -> familyHelper = this.familyMaleHelper;
            default -> throw new IllegalStateException(String.format("Gender %s is not supported", person.getSex()));
        }

        int personTag = gedcomCounter.getNewPersonTag();

        if (families.isEmpty()) {
            families.addAll(createAllFamiliesForPerson(person, personTag, familyHelper));
            writePersonData(person, personTag, output, families);
            return;
        }

        boolean childNotAdded = true;
        for (Family family : families) {
            if (family.areSpouses(person.getMotherId(), person.getFatherId())) {
                familyHelper.addChild(family, person.getId(), personTag);
                childNotAdded = false;
                break;
            }
        }

        if (childNotAdded) {
            createFamilyForChild(person, personTag, familyHelper)
                    .ifPresent(families::add);
        }


        for (String spouseId : person.getSpouseIds()) {
            boolean marriageDontExist = true;

            for (Family family : families) {
                if (family.areSpouses(person.getId(), spouseId)) {
                    marriageDontExist = false;
                    break;
                }
            }

            if (marriageDontExist) {
                families.add(familyHelper.createFamilyForSpouse(
                        gedcomCounter.getNewFamilyTag(), person.getId(), personTag, spouseId));
            }
        }

        families.forEach(family -> familyHelper.setTagIfSpouse(family, person.getId(), personTag));

        writePersonData(person, personTag, output, families);
    }

    private void writePersonData(Person person, int personTag, StringBuilder output, List<Family> families) {
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

        for (Family family : families) {
            if (family.isSpouse(person.getId())) {
                output.append(String.format(PARENT_PATTERN, family.getFamilyTag()));
            } else if (family.isChild(person.getId())) {
                output.append(String.format(CHILD_PATTERN, family.getFamilyTag()));
            }
        }
    }

    private List<Family> createAllFamiliesForPerson(Person person, int personTag, FamilyHelper familyHelper) {
        List<Family> families = new ArrayList<>();

        person.getSpouseIds()
                .forEach(spouseId ->
                        families.add(familyHelper.createFamilyForSpouse(
                                gedcomCounter.getNewFamilyTag(), person.getId(), personTag, spouseId)));

        createFamilyForChild(person, personTag, familyHelper)
                .ifPresent(families::add);

        return families;
    }

    private Optional<Family> createFamilyForChild(Person child, int childTag, FamilyHelper familyHelper) {
        if (child.getMotherId() != null || child.getFatherId() != null) {
            return Optional.of(familyHelper.createFamilyForChild(
                    gedcomCounter.getNewFamilyTag(), child.getId(), childTag, child.getMotherId(), child.getFatherId()));
        }

        return Optional.empty();
    }
}