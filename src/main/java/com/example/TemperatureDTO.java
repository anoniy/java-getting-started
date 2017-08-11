package com.example;

public class TemperatureDTO {

    private String tempIn;
    private String tempOut;
    private String measureDate;

    public TemperatureDTO(String tempIn, String tempOut, String measureDate) {
        this.tempIn = tempIn;
        this.tempOut = tempOut;
        this.measureDate = measureDate;
    }

    public String getTempIn() {
        return tempIn;
    }

    public String getTempOut() {
        return tempOut;
    }

    public String getMeasureDate() {
        return measureDate;
    }
}
