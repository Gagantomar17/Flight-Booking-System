package com.flightbooking.flight_module.Model;

public class documentDetails {

    private String passportNumber ;
    private String documentType = "PASSPORT" ;
    private String birthPlace ;
    private String issuanceLocation ;
    private String issuanceDate ;
    private String expiryDate ;
    private String issuanceCountry ;
    private String validityCountry ;
    private String nationality ;
    private boolean holder = true ;

    public boolean getHolder(){
        return holder ;
    }

    public void setHolder(boolean holder) {
        this.holder = holder;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIssuanceCountry() {
        return issuanceCountry;
    }

    public void setIssuanceCountry(String issuanceCountry) {
        this.issuanceCountry = issuanceCountry;
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public String getIssuanceLocation() {
        return issuanceLocation;
    }

    public void setIssuanceLocation(String issuanceLocation) {
        this.issuanceLocation = issuanceLocation;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getValidityCountry() {
        return validityCountry;
    }

    public void setValidityCountry(String validityCountry) {
        this.validityCountry = validityCountry;
    }

    
}
