package com.mytest.testtask.models;

public class SmsPayload {

    private String msisdnA;
    private String msisdnB;
    private String text;


    public String getMsisdnA() {
        return msisdnA;
    }

    public void setMsisdnA(String msisdnA) {
        this.msisdnA = msisdnA;
    }

    public String getMsisdnB() {
        return msisdnB;
    }

    public void setMsisdnB(String msisdnB) {
        this.msisdnB = msisdnB;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
