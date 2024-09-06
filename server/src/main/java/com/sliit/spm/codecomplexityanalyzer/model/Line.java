package com.sliit.spm.codecomplexityanalyzer.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Line {

    int lineNo;
    String data;
    int cs;
    int ctc;
    int cnc;
    int ci;
    int cps;
    int tw;
    int cr;
    int singleLineCommentsCount; // Count of single-line comments
    int multiLineCommentsCount;  // Count of multi-line comments

    @Override
    public String toString() {
        return "Line{" +
                "lineNo=" + lineNo +
                ", singleLineCommentsCount=" + singleLineCommentsCount +
                ", multiLineCommentsCount=" + multiLineCommentsCount +
                ", cs=" + cs +
                ", ctc=" + ctc +
                ", cnc=" + cnc +
                ", ci=" + ci +
                ", cps=" + cps +
                ", tw=" + tw +
                ", cr=" + cr +
                ", data=" + data +
                '}';
    }
}
