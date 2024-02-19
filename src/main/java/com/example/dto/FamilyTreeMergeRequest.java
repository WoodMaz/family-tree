package com.example.dto;

import com.example.models.FamilyTree;
import lombok.Getter;

import java.util.List;

@Getter
public class FamilyTreeMergeRequest {
    private List<String> familyTreeIds;
    private FamilyTree mergedTree;
}
