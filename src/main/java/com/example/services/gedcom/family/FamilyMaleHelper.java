package com.example.services.gedcom.family;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class FamilyMaleHelper extends FamilyHelper {
    @Override
    public Family createFamilyForSpouse(int familyTag, String personId, int personTag, String spouseId) {
        Family family = new Family(familyTag);

        family.setHusband(new Family.Member(personId, personTag));
        family.setWife(new Family.Member(spouseId));

        return family;
    }

    @Override
    public void setTagIfSpouse(Family family, String id, int tag) {
        if (family.getHusband() == null)
            return;

        if (family.getHusband().getId().equals(id)) {
            family.getHusband().setTag(tag);
        }
    }
}
