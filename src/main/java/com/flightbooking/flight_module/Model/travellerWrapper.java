package com.flightbooking.flight_module.Model;

public class travellerWrapper {
    private travellerDetail traveller ;

    public travellerWrapper(travellerDetail traveller){
        this.traveller = traveller ;
    }

    public void setTraveller(travellerDetail traveller) {
        this.traveller = traveller;
    }

    public travellerDetail getTraveller() {
        return traveller;
    }
}
