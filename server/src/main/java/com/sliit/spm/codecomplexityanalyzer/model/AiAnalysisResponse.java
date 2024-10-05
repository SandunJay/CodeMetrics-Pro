package com.sliit.spm.codecomplexityanalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AiAnalysisResponse {
        private String overview;
        private String completedCode;
        private String improvedCode;

}
