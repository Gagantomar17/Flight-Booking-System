package com.flightbooking.flight_module.Model;


public class flightWrapper {
    private flight_detail flightDetail ;
    private String requestBody ;

    public flightWrapper(flight_detail flightDetail , String requestBody){
        this.flightDetail = flightDetail ;
        this.requestBody = requestBody ;
    }

    public void setFlightDetail(flight_detail flightDetail){
        this.flightDetail = flightDetail ;
    }

    public void setRequestBody(String requestBody){
        this.requestBody = requestBody ;

    }

    public flight_detail getFlightDetail(){
        return flightDetail ;
    }

    public String getRequestBody(){
        return requestBody ;
    }

}