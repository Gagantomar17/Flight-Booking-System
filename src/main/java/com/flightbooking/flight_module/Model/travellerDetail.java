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

    public String getLastName() {
        return lastName;
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

    public String getNumber() {
        return number;
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

    public void setLastName(String lastName) {
        this.lastName = lastName.toUpperCase();
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setCountryCallingCode(String countryCallingCode) {
        this.countryCallingCode = countryCallingCode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
