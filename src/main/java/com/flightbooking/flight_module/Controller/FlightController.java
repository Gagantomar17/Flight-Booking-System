package com.flightbooking.flight_module.Controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.flightbooking.flight_module.Model.flightSearch;
import com.flightbooking.flight_module.Model.flightWrapper;
import com.flightbooking.flight_module.Service.amadeusAPI;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FlightController {

    private final amadeusAPI api ;

    @Autowired
    public FlightController(amadeusAPI api){
        this.api = api ;
    }

    @GetMapping("/search")
    public String flightSearchPage(Model model){
        model.addAttribute("flightSearch", new flightSearch());
        return "inputForm" ;
    }

    @PostMapping("/searchFlights")
    public String searchResult(Model model ,

    // below @RequestParam() annotation is used to fetch data from html/thymeleaf code 
    // by matching approrpiate (name="") tag value assigned 

        //  @RequestParam("source") String source ,
        //  @RequestParam("destination") String destination ,
        //  @RequestParam("date") String date 

    // below model attribute ammotaion bind the model and then we can use getter and setter to 
    // fetch desired data from the model created 
    
        @ModelAttribute flightSearch fSearch){
            String apiResponse ;
            String source = fSearch.getSource();
            String destination = fSearch.getDestination();
            String date = fSearch.getDate();
            List<flightWrapper> flights = new ArrayList<>(); 
            try{
                apiResponse = api.getFlights(source, destination, date);
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
                return "bookFlights" ;
            } 
    }

}
