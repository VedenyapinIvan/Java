package ru.via.decode.dao;

public class DecodeRecord {

    private String sourceValue;
    private String targetValue;

    // Empty constructor
    public DecodeRecord() {
    }

    public String getSourceValue() {
        return sourceValue;
    }

    public void setSourceValue(String sourceValue) {
        this.sourceValue = sourceValue;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    @Override
    public String toString() {
        return "DecodeRecord {sourceValue=" + sourceValue + "; targetValue=" + targetValue + "}";
    }
}
