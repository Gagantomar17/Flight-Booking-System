
package com.flightbooking.flight_module.Model;

public class travellerDetail {
    private String id = "1" ;
    private String dateOfBirth ;
    private String gender ;
    private String firstName ;
    private String lastName ;
    private String emailAddress ;
    private String deviceType = "MOBILE" ;
    private String countryCallingCode ;
    private String number ;
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


    public String getId(){
        return id ;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getCountryCallingCode() {
        return countryCallingCode;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public String getIssuanceLocation() {
        return issuanceLocation;
    }

    public String getIssuanceDate() {
        return issuanceDate;
    }

    public String getNumber() {
        return number;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getIssuanceCountry() {
        return issuanceCountry;
    }

    public String getValidityCountry() {
        return validityCountry;
    }

    public String getNationality() {
        return nationality;
    }

    public boolean getHolder(){
        return holder ;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender.toUpperCase();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.toUpperCase();
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.toUpperCase();
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace.toUpperCase();
    }

    public void setIssuanceLocation(String issuanceLocation) {
        this.issuanceLocation = issuanceLocation.toUpperCase();
    }

    public void setIssuanceDate(String issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setIssuanceCountry(String issuanceCountry) {
        this.issuanceCountry = issuanceCountry;
    }

    public void setValidityCountry(String validityCountry) {
        this.validityCountry = validityCountry;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

}
