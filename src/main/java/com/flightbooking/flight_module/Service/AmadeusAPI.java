package com.flightbooking.flight_module.Service;

import com.flightbooking.flight_module.Model.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AmadeusAPI {

    private static String responseBody = "null" ;
    private static String repriceResponse = "null";
    private static String accessToken = "null";

    @Value("${amadeus.oath.url}")
    private String oath_url ;

    @Value("${amadeus.client.id}")
    private String client_id ;

    @Value("${amadeus.client.secret}")
    private String client_secret ;

    @Value("${amadeus.reprice.url}")
    private String reprice_url ;

    @Value("${amadeus.booking.url}")
    private String booking_url ;

    public String repriceFlights(String response){
        System.out.println("Response pass : " + response);
        JSONObject jsonObject = new JSONObject();
        JSONObject responseObj = new JSONObject(response);
        JSONArray responseArray = new JSONArray();
        responseArray.put(responseObj);
        jsonObject.put("flightOffers", responseArray);
        jsonObject.put("type", "flight-offers-pricing");
        
        JSONObject responseBody = new JSONObject();
        responseBody.put("data", jsonObject);
        System.out.println("Request Body : " + responseBody.toString());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpPost post = new HttpPost(reprice_url);
            post.addHeader("Authorization", "Bearer " + accessToken);
            post.addHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(responseBody.toString()));

            try(CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                repriceResponse = EntityUtils.toString(httpResponse.getEntity()); 
                System.out.println("Reprice Reprice Response : " + repriceResponse);
            } catch (Exception e) {
                e.printStackTrace();
                repriceResponse = "Error : " + e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            repriceResponse = "Error Error : " + e.getMessage() ;
        }

        return repriceResponse ;
    }

    public boolean isPriceUnchanged(String repriceResponse , String price){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(repriceResponse);
            String reprice = rootNode.path("data").path("flightOffers").get(0).path("price").path("total").asText()  ;
            System.out.println("Fetched reprice value : " + reprice);
            price = price.replaceAll("[^0-9.]", "");
            System.out.println("Fetched price value : " + price);
            float beforePrice = Float.parseFloat(price);
            float afterPrice = Float.parseFloat(reprice);
            if(beforePrice == afterPrice){
                return true ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false ;
    }

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
                if (!tokenJsonObject.has("access_token")) {
                    throw new RuntimeException("Access token not found in response: " + tokenJsonResponse);
                }
                accessToken = tokenJsonObject.getString("access_token");

                System.out.println("Here is access token " + accessToken) ;

                // Use the access token to fetch flight offers
                HttpGet flightRequest = new HttpGet("https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode="+source+"&destinationLocationCode="+destination+"&departureDate="+date+"&adults=1&nonStop=false&max=250");
                flightRequest.addHeader("Authorization", "Bearer " + accessToken);
                flightRequest.addHeader("Accept", "application/vnd.amadeus+json"); 

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

    public List<flightWrapper> responseParsing(String responseBody){
        ObjectMapper objectMapper = new ObjectMapper();
        List<flightWrapper> flights = new ArrayList<>();
        //List<flight_detail> flights = new ArrayList<>();
        try{
            JsonNode rootNode = objectMapper.readTree(responseBody); // complete api response
            JsonNode dataArray = rootNode.get("data"); // it contains the flights data 
        
            if(dataArray !=null && dataArray.isArray() ){
                for(JsonNode flightNode : dataArray){

                    JSONObject flightObject = new JSONObject(flightNode.toString());
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

                    flights.add(new flightWrapper(flightModel, flightObject.toString())); 
                    
                }
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return flights ;
    }

    public String orderReqBody(String repriceResponse , travellerDetail traveller , agentDetails agent){
        JSONObject requestBody = new JSONObject();

        JSONObject jsonObject = new JSONObject(repriceResponse);
        JSONArray flightOfferArray = jsonObject.getJSONObject("data").getJSONArray("flightOffers") ;

        JSONObject data = new JSONObject();
        data.put("type", "flight-order");
        data.put("flightOffers", flightOfferArray);

        JSONObject travellerObj = new JSONObject();
        travellerObj.put("id", traveller.getId());
        travellerObj.put("dateOfBirth", traveller.getDateOfBirth());

        JSONObject name = new JSONObject();
        name.put("firstName", traveller.getFirstName());
        name.put("lastName", traveller.getLastName());
        travellerObj.put("name", name);

        travellerObj.put("gender", traveller.getGender());

        JSONObject phone = new JSONObject();
        phone.put("deviceType", traveller.getDeviceType());
        phone.put("countryCallingCode", traveller.getCountryCallingCode());
        phone.put("number", traveller.getNumber());

        JSONArray phoneArray = new JSONArray();
        phoneArray.put(phone);

        JSONObject contact = new JSONObject();
        contact.put("emailAddress", traveller.getEmailAddress());
        contact.put("phones", phoneArray);

        travellerObj.put("contact", contact);

        JSONObject document = new JSONObject();
        document.put("documentType", traveller.getDocumentType());
        document.put("birthPlace", traveller.getBirthPlace());
        document.put("issuanceLocation", traveller.getIssuanceLocation());
        document.put("issuanceDate", traveller.getIssuanceDate());
        document.put("number", traveller.getPassportNumber());
        document.put("expiryDate", traveller.getExpiryDate());
        document.put("issuanceCountry", traveller.getIssuanceCountry());
        document.put("validityCountry", traveller.getValidityCountry());
        document.put("nationality", traveller.getNationality());
        document.put("holder", traveller.getHolder());

        JSONArray documentArray = new JSONArray();
        documentArray.put(document);

        travellerObj.put("documents", documentArray);

        JSONArray travellerArray = new JSONArray();
        travellerArray.put(travellerObj);
        data.put("travelers", travellerArray);

        JSONObject remarks = new JSONObject();
        JSONObject general = new JSONObject();
        general.put("subType", "GENERAL_MISCELLANEOUS");
        general.put("text", "ONLINE BOOKING FROM Tomar Travel Pvt Ltd");
        JSONArray generalArray = new JSONArray();
        generalArray.put(general);
        remarks.put("general", generalArray);

        data.put("remarks", remarks);

        JSONObject ticketingAgreement = new JSONObject();
        ticketingAgreement.put("option", "DELAY_TO_CANCEL");
        ticketingAgreement.put("delay", "6D");

        data.put("ticketingAgreement", ticketingAgreement);

        JSONObject contacts = new JSONObject();
        JSONObject addresseeName = new JSONObject();
        addresseeName.put("firstName", agent.getFirstName());
        addresseeName.put("lastName", agent.getLastName());
        contacts.put("addresseeName", name);
        contacts.put("companyName", agent.getCompanyName());
        contacts.put("purpose", agent.getPurpose());
        JSONObject agentPhone = new JSONObject();
        agentPhone.put("deviceType", agent.getDeviceType());
        agentPhone.put("countryCallingCode", agent.getCountryCallingCode());
        agentPhone.put("number", agent.getNumber());

        JSONArray agentPhoneArray = new JSONArray();
        agentPhoneArray.put(agentPhone);
        contacts.put("phones", agentPhoneArray);
        contacts.put("emailAddress", agent.getEmailAddress());

        JSONObject address = new JSONObject();
        JSONArray lines = new JSONArray();
        lines.put(agent.getLines());
        address.put("lines", lines);
        address.put("postalCode", agent.getPostalCode());
        address.put("cityName", agent.getCityName());
        address.put("countryCode", agent.getCountryCode());

        contacts.put("address", address);

        JSONArray contactsArray = new JSONArray();
        contactsArray.put(contacts);
        data.put("contacts", contactsArray);

        requestBody.put("data", data);
    
        return requestBody.toString() ;
    }

    public String bookFlight(String reqBody){
        String bookingResponse = "null" ;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(booking_url);
            post.addHeader("Authorization", "Bearer " + accessToken);
            post.addHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(reqBody));

            try (CloseableHttpResponse httpResponse = httpClient.execute(post) ) {
                bookingResponse = EntityUtils.toString(httpResponse.getEntity()) ;
            } catch (Exception e) {
                e.printStackTrace();
                bookingResponse = "Booking error : " + e.getMessage() ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            bookingResponse = "booking order error : " + e.getLocalizedMessage();
        }
        return bookingResponse ;
    }

    public bookingDetails orderResponseParsing(String bookingResponse){
        bookingDetails bookingData = new bookingDetails();
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode rootNode = mapper.readTree(bookingResponse);
            JsonNode dataNode = rootNode.get("data");

            bookingData.setOrderId(dataNode.get("id").asText());
            bookingData.setQueuingOfficeId(dataNode.get("queuingOfficeId").asText());

            JsonNode recordsArray = dataNode.get("associatedRecords");
            if(recordsArray != null && recordsArray.isArray()){
                for(JsonNode record : recordsArray){
                    bookingData.setCreationDate(record.get("creationDate").asText());
                }
            }

            JsonNode flightOfferArray = dataNode.get("flightOffers");

            if(flightOfferArray != null && flightOfferArray.isArray()){
                for(JsonNode flightOffer : flightOfferArray){
                    JsonNode itinerariesArray = flightOffer.get("itineraries");

                    if(itinerariesArray != null && itinerariesArray.isArray()){
                        for(JsonNode itinerary : itinerariesArray){
                            JsonNode segmentsArray = itinerary.get("segments");

                            if(segmentsArray != null && segmentsArray.isArray()){
                                for(JsonNode segment : segmentsArray){
                                    bookingData.setDeparture(segment.get("departure").get("iataCode").asText());
                                    bookingData.setDepartureTime(segment.get("departure").get("at").asText());
                                    bookingData.setArrival(segment.get("arrival").get("iataCode").asText());
                                    bookingData.setArrivalTime(segment.get("arrival").get("at").asText());
                                    bookingData.setFlightNumber(segment.get("number").asText());
                                    bookingData.setCarrierCode(segment.get("carrierCode").asText());
                                }
                            }
                        }
                    }

                    JsonNode priceNode = flightOffer.get("price");
                    bookingData.setTotalPrice(priceNode.get("total").asText());
                    bookingData.setBillingCurrency(priceNode.get("currency").asText());

                    JsonNode travelerPricingsArray = flightOffer.get("travelerPricings");
                    if(travelerPricingsArray != null && travelerPricingsArray.isArray()){
                        for (JsonNode travelerPricing : travelerPricingsArray){
                            JsonNode fareDetailsBySegmentArray = travelerPricing.get("fareDetailsBySegment");

                            if(fareDetailsBySegmentArray != null && fareDetailsBySegmentArray.isArray()){
                                for (JsonNode fareDetail : fareDetailsBySegmentArray){
                                    bookingData.setBaggage(fareDetail.get("includedCheckedBags").get("weight").asText());
                                    bookingData.setBaggageUnit(fareDetail.get("includedCheckedBags").get("weightUnit").asText());
                                }
                            }
                        }
                    }
                }
            }

            JsonNode travelersArray = dataNode.get("travelers");
            if(travelersArray != null && travelersArray.isArray()){
                for(JsonNode traveler : travelersArray){
                    bookingData.setTravelerId(traveler.get("id").asText());
                    bookingData.setGender(traveler.get("gender").asText());
                    bookingData.setDateOfBirth(traveler.get("dateOfBirth").asText());
                    bookingData.setFirstName(traveler.get("name").get("firstName").asText());
                    bookingData.setLastName(traveler.get("name").get("lastName").asText());

                }
            }

            JsonNode remarksArray = dataNode.get("remarks").get("general");
            if(remarksArray != null && remarksArray.isArray()){
                for(JsonNode general : remarksArray){
                    bookingData.setRemarks(general.get("text").asText());
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return bookingData ;
    }
}
