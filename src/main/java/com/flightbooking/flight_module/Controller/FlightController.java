package com.flightbooking.flight_module.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.flightbooking.flight_module.Model.flightSearch;
import com.flightbooking.flight_module.Service.amadeusAPI;
import com.flightbooking.flight_module.Model.flight_detail;
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
         @RequestParam("source") String source ,
         @RequestParam("destination") String destination ,
         @RequestParam("date") String date ){
            String apiResponse ;
            List<flight_detail> flights = new ArrayList<>(); 
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

}
