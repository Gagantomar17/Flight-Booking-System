package com.flightbooking.flight_module.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightbooking.flight_module.Model.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.flightbooking.flight_module.Service.AmadeusAPI;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FlightController {

    private final AmadeusAPI api ;

    @Autowired
    public FlightController(AmadeusAPI api){

        this.api = api ;
    }

    @GetMapping("/search")
    public String flightSearchPage(Model model){
        model.addAttribute("flightSearch", new flightSearch());
        return "inputForm" ;
    }

    @PostMapping("/searchFlights")
    public String searchResult(Model model ,
    
        @ModelAttribute flightSearch fSearch){
            String apiResponse ;
            String source = fSearch.getSource();
            String destination = fSearch.getDestination();
            String date = fSearch.getDate();
            int adult = fSearch.getAdult();
            int child = fSearch.getChild();
            int infant = fSearch.getInfant();
            List<flightWrapper> flights = new ArrayList<>(); 
            try{
                apiResponse = api.getFlights(source, destination, date, adult, child, infant);
                flights = api.responseParsing(apiResponse);

            }catch(Exception e){
                apiResponse = "Error occur" + e.getMessage();
            }
            
            model.addAttribute("source", source);
            model.addAttribute("destination", destination);
            model.addAttribute("date", date);
            model.addAttribute("response", apiResponse);
            model.addAttribute("flights", flights);

            return "flightResults" ;

    }

    @PostMapping("/bookflights")
    public String bookFlight(Model model , 
        @RequestParam("responseBody") String response ,
        @RequestParam("beforePrice") String price ){
            String repriceResponse = null ;
            try{
                repriceResponse = api.repriceFlights(response);
            }catch(Exception e){
                repriceResponse = " Error occured " + e.getMessage();
            }
            model.addAttribute("repriceResponse", repriceResponse);
            boolean reprice = api.isPriceUnchanged(repriceResponse, price) ;

            if(!reprice){
                
                return "repriceFlights" ;
            }else{
                try {
                    int travelers = new ObjectMapper().readTree(repriceResponse).path("data").path("flightOffers").get(0).path("travelerPricings").size();

                    travellerWrapper traveller = new travellerWrapper();
                    List<travellerDetail> travellerList = new ArrayList<>();
        
                    for (int i = 0; i < travelers; i++) {
                        travellerList.add(new travellerDetail());
                    }
        
                    traveller.setTravellerList(travellerList);
                    model.addAttribute("travellerWrapper", traveller);
                    System.out.println("Traveller List Size: " + travellerList.size());
                } catch (Exception e) {
                    // TODO: handle exception
                }
                return "bookFlights" ;
            } 
    }

    @PostMapping("/submitform")
    public String createOrder(Model model ,
    @ModelAttribute travellerWrapper traveller ,
    @ModelAttribute agentDetails agent ,
    @RequestParam("repriceResponse") String repriceResponse){
        String reqBody = "null" ;
        String resBody = "null" ;
        bookingDetails bookingData = null;
        try {
            reqBody = api.orderReqBody(repriceResponse , traveller , agent) ;
            System.out.println("create order request : " + reqBody);
            try {
                resBody = api.bookFlight(reqBody) ;
                System.out.println("create order response body : " + resBody);
                bookingData = api.orderResponseParsing(resBody) ;
                
            } catch (Exception e) {
                resBody = e.getMessage() ;
                System.out.println("create order response body : " + resBody);
            }
        } catch (Exception e) {
            resBody = e.getMessage();
            System.out.println("create order response body : " + resBody);
        }
        if(!new JSONObject(resBody).has("data")){
            System.out.println("create order response body : " + resBody);
            model.addAttribute("responseBody", resBody);
            return "errorPage" ;
        }
        //System.out.println("create order response body : " + resBody);

        model.addAttribute("responseBody", resBody);
        model.addAttribute("bookingData" , bookingData);
        return "orderPage" ;
    }

}
