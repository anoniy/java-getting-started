package com.example;

public class TempDTO {

    private String tempIn;
    private String tempOut;

    public TempDTO(String tempIn, String tempOut) {
        this.tempIn = tempIn;
        this.tempOut = tempOut;
    }

    public String getTempIn() {
        return tempIn;
    }

    public String getTempOut() {
        return tempOut;
    }
}
