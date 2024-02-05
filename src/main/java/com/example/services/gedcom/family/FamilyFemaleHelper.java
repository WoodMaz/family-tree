package com.example.services.gedcom.family;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class FamilyFemaleHelper extends FamilyHelper {
    @Override
    public Family createFamilyForSpouse(int familyTag, String personId, int personTag, String spouseId) {
        Family family = new Family(familyTag);

        family.setWife(new Family.Member(personId, personTag));
        family.setHusband(new Family.Member(spouseId));

        return family;
    }

    @Override
    public void setTagIfSpouse(Family family, String id, int tag) {
        if (family.getWife() == null)
            return;

        if (family.getWife().getId().equals(id)) {
            family.getWife().setTag(tag);
        }
    }
}
