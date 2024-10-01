package com.sliit.spm.codecomplexityanalyzer.model;

public class GeminiAnalysisResponse {
        private String overview;

        public GeminiAnalysisResponse(String overview) {
            this.overview = overview;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }
}
