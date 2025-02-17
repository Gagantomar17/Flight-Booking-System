package com.flightbooking.flight_module.Model;

public class bookingDetails {

    private String orderId;
    private String queuingOfficeId;
    private String creationDate;
    private String departure ;
    private String departureTime;
    private String arrival;
    private String arrivalTime;
    private String carrierCode ;
    private String flightNumber;
    private String baggage;
    private String baggageUnit;

    private String totalPrice;
    private String billingCurrency;

    private String travelerId;
    private String firstName;
    private String lastName;
    private String gender ;
    private String dateOfBirth;
    
    private String remarks;

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setQueuingOfficeId(String queuingOfficeId) {
        this.queuingOfficeId = queuingOfficeId;
    }

    public void setBaggage(String baggage) {
        this.baggage = baggage;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public void setBaggageUnit(String baggageUnit) {
        this.baggageUnit = baggageUnit;
    }

    public void setBillingCurrency(String billingCurrency) {
        this.billingCurrency = billingCurrency;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setTravelerId(String travelerId) {
        this.travelerId = travelerId;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getArrival() {
        return arrival;
    }

    public String getQueuingOfficeId() {
        return queuingOfficeId;
    }

    public String getDeparture() {
        return departure;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getBaggage() {
        return baggage;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public String getBaggageUnit() {
        return baggageUnit;
    }

    public String getBillingCurrency() {
        return billingCurrency;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getTravelerId() {
        return travelerId;
    }
}
