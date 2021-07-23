package com.mytest.testtask.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(schema = "main", name="subscriber")
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column
    private String fathersName;

    @Column
    private String countryCode;
    @Column
    private String operatorCode;
    @Column
    private String onlyNumber;
    @Column
    private String msisdn;
    @Column
    private LocalDate registrationDate;

    @Column
    private String timeZone;
    @Column
    private String Language;

    @Column
    private String documentSerie;
    @Column
    private Integer documentNumber;
    @Column
    private String documentGivenOrganization;
    @Column
    private LocalDate documentGivenDate;
    @Column
    private LocalDate documentValidityDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFathersName() {
        return fathersName;
    }

    public void setFathersName(String fathersName) {
        this.fathersName = fathersName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOnlyNumber() {
        return onlyNumber;
    }

    public void setOnlyNumber(String onlyNumber) {
        this.onlyNumber = onlyNumber;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getDocumentSerie() {
        return documentSerie;
    }

    public void setDocumentSerie(String documentSerie) {
        this.documentSerie = documentSerie;
    }

    public Integer getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(Integer documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentGivenOrganization() {
        return documentGivenOrganization;
    }

    public void setDocumentGivenOrganization(String documentGivenOrganization) {
        this.documentGivenOrganization = documentGivenOrganization;
    }

    public LocalDate getDocumentGivenDate() {
        return documentGivenDate;
    }

    public void setDocumentGivenDate(LocalDate documentGivenDate) {
        this.documentGivenDate = documentGivenDate;
    }

    public LocalDate getDocumentValidityDate() {
        return documentValidityDate;
    }

    public void setDocumentValidityDate(LocalDate documentValidityDate) {
        this.documentValidityDate = documentValidityDate;
    }



    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }
}
