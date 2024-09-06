package com.sliit.spm.codecomplexityanalyzer.model;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }
    public int getSingleLineCommentsCount() {
        return singleLineCommentsCount;
    }

    public void setSingleLineCommentsCount(int singleLineCommentsCount) {
        this.singleLineCommentsCount = singleLineCommentsCount;
    }

    public int getMultiLineCommentsCount() {
        return multiLineCommentsCount;
    }

    public void setMultiLineCommentsCount(int multiLineCommentsCount) {
        this.multiLineCommentsCount = multiLineCommentsCount;
    }

    public int getCs() {
        return cs;
    }

    public void setCs(int cs) {
        this.cs = cs;
    }

    public int getCtc() {
        return ctc;
    }

    public void setCtc(int ctc) {
        this.ctc = ctc;
    }

    public int getCnc() {
        return cnc;
    }

    public void setCnc(int cnc) {
        this.cnc = cnc;
    }

    public int getCi() {
        return ci;
    }

    public void setCi(int ci) {
        this.ci = ci;
    }

    public int getCps() {
        return cps;
    }

    public void setCps(int cps) {
        this.cps = cps;
    }

    public int getTw() {
        return tw;
    }

    public void setTw(int tw) {
        this.tw = tw;
    }

    public int getCr() {
        return cr;
    }

    public void setCr(int cr) {
        this.cr = cr;
    }
    public boolean isComment() {
        return data.trim().startsWith("//");
    }
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
