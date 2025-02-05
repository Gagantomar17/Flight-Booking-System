package com.flightbooking.flight_module.Service;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONObject; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightbooking.flight_module.Model.flight_detail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class amadeusAPI {

    private static String responseBody = "null" ;

    @Value("${amadeus.oath.url}")
    private String oath_url ;

    @Value("${amadeus.client.id}")
    private String client_id ;

    @Value("${amadeus.client.secret}")
    private String client_secret ;

    public String getFlights(String source, String destination, String date){
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Obtain the access token using POST request
            HttpPost post = new HttpPost(oath_url);
            post.addHeader("Content-Type", "application/x-www-form-urlencoded");
            StringEntity entity = new StringEntity("grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret);
            post.setEntity(entity);

            try (CloseableHttpResponse tokenResponse = httpClient.execute(post)) {
                String tokenJsonResponse = EntityUtils.toString(tokenResponse.getEntity());
                JSONObject tokenJsonObject = new JSONObject(tokenJsonResponse);
                String accessToken = tokenJsonObject.getString("access_token");

                // Use the access token to fetch flight offers
                HttpGet flightRequest = new HttpGet("https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode="+source+"&destinationLocationCode="+destination+"&departureDate="+date+"&adults=1&nonStop=false&max=250");
                flightRequest.addHeader("Authorization", "Bearer " + accessToken);

                try (CloseableHttpResponse flightResponse = httpClient.execute(flightRequest)) {
                    responseBody = EntityUtils.toString(flightResponse.getEntity());
                    System.out.println("Flight Offers Response: " + responseBody);
                }
            } catch (Exception e){
                e.printStackTrace();
                responseBody = "Error occurred: " + e.getMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            responseBody = "Error occurred: " + e.getMessage();
        }
        return responseBody;
    };

    public List<flight_detail> responseParsing(String responseBody){
        ObjectMapper objectMapper = new ObjectMapper();
        List<flight_detail> flights = new ArrayList<>();
        try{
            JsonNode rootNode = objectMapper.readTree(responseBody); // complete api response
            JsonNode dataArray = rootNode.get("data"); // it contains the flights data 
        
            if(dataArray !=null && dataArray.isArray() ){
                for(JsonNode flightNode : dataArray){

                    flight_detail flightModel = new flight_detail();
                    JsonNode itinerariesNode = flightNode.path("itineraries");
                    JsonNode travelerPricingsNode = flightNode.path("travelerPricings");
                    boolean oneWay = flightNode.path("oneWay").asBoolean();
                    int avalSeats = flightNode.path("numberOfBookableSeats").asInt(0);
                    String price = flightNode.get("price").path("total").asText("");
                    String currency = flightNode.get("price").path("currency").asText("");
                    price = price + " " + currency ;

                    flightModel.setAvalSeats(avalSeats);
                    flightModel.setOneWay(oneWay);
                    flightModel.setPrice(price);

                    if (itinerariesNode != null && itinerariesNode.isArray()) {

                        for (JsonNode itineraryNode : itinerariesNode) {
                            String carrierCode = itineraryNode.path("carrierCode").asText("");
                            String duration = itineraryNode.path("duration").asText("").substring(2);

                            flightModel.setCarrierCode(carrierCode);
                            flightModel.setDuration(duration);

                            JsonNode segmentsNode = itineraryNode.path("segments");
                            if (segmentsNode != null && segmentsNode.isArray()) {   
                                for(JsonNode segmentNode : segmentsNode){
                                    String flightNo = segmentNode.path("number").asText("");
                                    String src = segmentNode.path("departure").path("iataCode").asText(""); // Get source IATA code
                                    String dest = segmentNode.path("arrival").path("iataCode").asText(""); // Get destination IATA code
                                    String depTime = segmentNode.path("departure").path("at").asText("").substring(11, 19); 
                                    String arrTime = segmentNode.path("arrival").path("at").asText("").substring(11, 19); 
                                    int stops = segmentNode.path("numberOfStops").asInt();

                                    flightModel.setSrc(src);
                                    flightModel.setDest(dest);
                                    flightModel.setDepTime(depTime);
                                    flightModel.setArrTime(arrTime);
                                    flightModel.setStops(stops);
                                    flightModel.setFlightNo(flightNo);
                                }
                            }
                        }
                    }

                    if(travelerPricingsNode != null && travelerPricingsNode.isArray()){

                        for(JsonNode travelerPricingNode : travelerPricingsNode){
                            JsonNode fareDetailsBySegment = travelerPricingNode.path("fareDetailsBySegment");

                            if(fareDetailsBySegment !=null && fareDetailsBySegment.isArray()){
                                for(JsonNode fareDetailBySegment : fareDetailsBySegment){
                                    String cabin = fareDetailBySegment.path("cabin").asText("");
                                    String classs = fareDetailBySegment.path("class").asText("");
                                    String bag = fareDetailBySegment.path("includedCheckedBags").path("weight").asText("0");
                                    String baggageUnit = fareDetailBySegment.path("weightUnit").asText("KG");
                                    String baggage = bag + " " + baggageUnit ; 

                                    flightModel.setBaggage(baggage);
                                    flightModel.setCabin(cabin);
                                    flightModel.setClasss(classs);
                                }
                                
                            }

                        }
                        
                    }

                    flights.add(flightModel); 
                    
                }
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return flights ;
    }

}
