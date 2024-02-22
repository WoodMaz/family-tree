package com.example.services;

import com.example.config.security.JwtService;
import com.example.enums.Sex;
import com.example.models.FamilyTree;
import com.example.models.Person;
import com.example.repositories.FamilyTreeRepository;
import com.example.services.gedcom.GedcomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FamilyTreeServiceTest {
    @Mock
    private JwtService jwtService;
    @Mock private UserService userService;
    @Mock private PersonService personService;
    @Mock private GedcomService gedcomService;
    @Mock private AccessService accessService;
    @Mock private FamilyTreeRepository familyTreeRepository;

    private FamilyTreeService familyTreeService;

    @BeforeEach
    public void setup() {
        familyTreeService = new FamilyTreeService(
                jwtService, userService, personService, gedcomService, accessService, familyTreeRepository);
    }
    
    @Test
    void mergeFamilyTrees_test() throws AuthenticationException {
        List<String> familyTreeIds = new ArrayList<>();
        familyTreeIds.add("1");
        familyTreeIds.add("2");
        FamilyTree mergedTree = new FamilyTree("3", "name", "description");
        String token = "token";

        List<Person> people1 = new ArrayList<>();
        Person person1 = new Person("personId1", "person", "A",
                Sex.MALE, LocalDate.of(1965, 11, 16), null,
                "motherId", "fatherId1", new HashSet<>(List.of("wifeId1")), null, "1");

        Person wife1 = new Person("wifeId1", "wife", "A",
                Sex.FEMALE, LocalDate.of(1965, 11, 16), null,
                null, null, new HashSet<>(List.of("personId1")), null, "1");

        Person mother1 = new Person("motherId1", "mother", "A",
                Sex.FEMALE, LocalDate.of(1947, 4, 28), null,
                "grandmaId1", null, new HashSet<>(List.of("fatherId1")), null, "1");

        Person father1 = new Person("fatherId1", "father", "A",
                Sex.MALE, LocalDate.of(1946, 4, 15), null,
                null, null, new HashSet<>(List.of("motherId1")), null, "1");

        Person grandma1 = new Person("grandmaId1", "grandma", "A",
                Sex.FEMALE, LocalDate.of(1946, 4, 15), null,
                null, null, new HashSet<>(List.of("motherId1")), null, "1");

        people1.add(person1);
        people1.add(wife1);
        people1.add(mother1);
        people1.add(father1);
        people1.add(grandma1);

        List<Person> people2 = new ArrayList<>();
        Person person2 = new Person("personId2", "person", "A",
                Sex.MALE, LocalDate.of(1965, 11, 16), null,
                null, null, new HashSet<>(List.of("wifeId2")), null, "2");
        
        Person wife2 = new Person("wifeId2", "wife", "A",
                Sex.FEMALE, LocalDate.of(1965, 11, 16), null,
                "motherInLawId2", "fatherInLawId2", new HashSet<>(List.of("personId2")), null, "2");
        
        Person mother2 = new Person("motherId2", "mother", "A",
                Sex.FEMALE, LocalDate.of(1947, 4, 28), null,
                null, null, new HashSet<>(List.of("fatherId2")), null, "2");
        
        Person father2 = new Person("fatherId2", "father", "B",
                Sex.MALE, LocalDate.of(1946, 4, 15), null,
                null, null, new HashSet<>(List.of("motherId2")), null, "2");

        Person motherInLaw2 = new Person("motherInLawId2", "motherInLaw", "B",
                Sex.FEMALE, LocalDate.of(1947, 4, 28), null,
                null, "grandpaId", new HashSet<>(List.of("fatherInLawId2")), null, "2");

        Person fatherInLawId2 = new Person("fatherInLawId2", "fatherInLaw", "B",
                Sex.MALE, LocalDate.of(1946, 4, 15), null,
                null, null, new HashSet<>(List.of("motherInLawId2")), null, "2");

        people2.add(person2);
        people2.add(wife2);
        people2.add(mother2);
        people2.add(father2);
        people2.add(motherInLaw2);
        people2.add(fatherInLawId2);

        personService = mock(PersonService.class);
        when(personService.getAllByFamilyTreeId("1")).thenReturn(people1);
        when(personService.getAllByFamilyTreeId("2")).thenReturn(people2);

        when(personService.copyToOtherFamilyTree(person1, mergedTree.getId())).thenReturn(copyToOtherTree(person1, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(wife1, mergedTree.getId())).thenReturn(copyToOtherTree(wife1, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(mother1, mergedTree.getId())).thenReturn(copyToOtherTree(mother1, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(father1, mergedTree.getId())).thenReturn(copyToOtherTree(father1, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(grandma1, mergedTree.getId())).thenReturn(copyToOtherTree(grandma1, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(person2, mergedTree.getId())).thenReturn(copyToOtherTree(person2, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(wife2, mergedTree.getId())).thenReturn(copyToOtherTree(wife2, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(mother2, mergedTree.getId())).thenReturn(copyToOtherTree(mother2, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(father2, mergedTree.getId())).thenReturn(copyToOtherTree(father2, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(motherInLaw2, mergedTree.getId())).thenReturn(copyToOtherTree(motherInLaw2, mergedTree.getId()));
        when(personService.copyToOtherFamilyTree(fatherInLawId2, mergedTree.getId())).thenReturn(copyToOtherTree(fatherInLawId2, mergedTree.getId()));

        List<Person> mergedPeople = familyTreeService.mergeFamilyTreesForTest(familyTreeIds, mergedTree, token);

        assertEquals(7, mergedPeople.size());
    }


    private static Person copyToOtherTree(Person person, String mergedTreeId) {
        Person newPerson = new Person(person, mergedTreeId);
        newPerson.setId("3");
        return newPerson;
    }
    
}
