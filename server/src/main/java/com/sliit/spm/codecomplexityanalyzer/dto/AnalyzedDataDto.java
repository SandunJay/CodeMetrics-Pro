package com.sliit.spm.codecomplexityanalyzer.dto;

import com.sliit.spm.codecomplexityanalyzer.model.AiAnalysisResponse;
import lombok.Data;

import java.util.Set;

@Data
public class AnalyzedDataDto {
    private String language;
    private Set<String> patterns;
    private AiAnalysisResponse aiAnalysisResponse;
}