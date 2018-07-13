package com.bus.huyma.hbus.activity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectionsTransitJSONParser {

    private String distanceTransit,durationTransit, startAddress,
            endAddress,arrivalTime, departureTime,status,travel_mode;
    private ArrayList<String> instructionsMove = new ArrayList<>();
    private ArrayList<String> codeRouteBus = new ArrayList<>();
    private ArrayList<String> nameBusStop = new ArrayList<>();
    private ArrayList<LatLng> listBusStop = new ArrayList<>();

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude and some information*/
    public List<List<HashMap<String, String>>> parseRespondeTransit(JSONObject jsonObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            jRoutes = jsonObject.getJSONArray("routes");
            status = jsonObject.getString("status");
            //Get all routes list
            for(int i=0; i<jRoutes.length(); i++){
                jLegs = ((JSONObject)jRoutes.get(i)).getJSONArray("legs");
                ArrayList<HashMap<String, String>> path = new ArrayList<>();

                //Get all element legs
                for(int j=0; j<jLegs.length(); j++){
                    String distance="", duration="",end_address="",start_address="",arrival_time="",departure_time="";
                    JSONObject JsonObjectLegs = ((JSONObject) jLegs.get(j));
                    jSteps = JsonObjectLegs.getJSONArray("steps");
                    distance = ((JSONObject)JsonObjectLegs.get("distance")).getString("text");
                    duration = ((JSONObject)JsonObjectLegs.get("duration")).getString("text");
                    start_address = JsonObjectLegs.getString("start_address");
                    end_address = JsonObjectLegs.getString("end_address");
                    arrival_time = ((JSONObject)JsonObjectLegs.get("arrival_time")).getString("text");
                    departure_time = ((JSONObject)JsonObjectLegs.get("departure_time")).getString("text");

                    //Assign all info data for origin init variable
                    distanceTransit = distance;
                    durationTransit = duration;
                    startAddress = start_address;
                    endAddress = end_address;
                    arrivalTime = arrival_time;
                    departureTime = departure_time;

                    //Get all element step
                    for(int k=0; k<jSteps.length(); k++){
                        String polyline = "";
                        String parentInstructions;
                        JSONObject jsonObjectStep = ((JSONObject)jSteps.get(k));
                        parentInstructions = jsonObjectStep.getString("html_instructions");
                        polyline = ((JSONObject)jsonObjectStep.get("polyline")).getString("points");
                        travel_mode = (((JSONObject) jSteps.get(k)).getString("travel_mode"));
                        List<LatLng> list= decodePoly(polyline);

                        for(int l=0; l<list.size(); l++){
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString(list.get(l).latitude));
                            hm.put("lng", Double.toString(list.get(l).longitude));
                            path.add(hm);
                        }

                        if(travel_mode.equals("WALKING")){
                            parentInstructions += " :\n";
                            JSONArray jWalkingSubSteps = jsonObjectStep.getJSONArray("steps");
                            for(int m=0; m< jWalkingSubSteps.length(); m++){
                                JSONObject jWalkingSubStepsObject = ((JSONObject)jWalkingSubSteps.get(m));
                                if(jWalkingSubStepsObject.has("html_instructions")){
                                    String stringInstructions = jWalkingSubStepsObject.getString("html_instructions");
                                    parentInstructions += stringInstructions+"\n";
                                }
                            }
                            instructionsMove.add(parentInstructions);
                            codeRouteBus.add("0");
                        }

                        if(travel_mode.equals("TRANSIT")){
                            JSONObject objectTransitDetail = ((JSONObject)jsonObjectStep.get("transit_details"));
                            JSONObject arrivalLocationObject = objectTransitDetail.getJSONObject("arrival_stop").getJSONObject("location");
                            JSONObject departureLocationObject = objectTransitDetail.getJSONObject("departure_stop").getJSONObject("location");

                            Double latDeparture = departureLocationObject.getDouble("lat");
                            Double lngDeparture = departureLocationObject.getDouble("lng");
                            String nameDeparture = "Trạm " + objectTransitDetail.getJSONObject("departure_stop").getString("name");
                            LatLng departure = new LatLng(latDeparture, lngDeparture);

                            Double latArrival = arrivalLocationObject.getDouble("lat");
                            Double lngArrival = arrivalLocationObject.getDouble("lng");
                            String nameArrival = "Trạm " + objectTransitDetail.getJSONObject("arrival_stop").getString("name");
                            LatLng arrival = new LatLng(latArrival, lngArrival);

                            nameBusStop.add(nameDeparture);
                            nameBusStop.add(nameArrival);
                            listBusStop.add(departure);
                            listBusStop.add(arrival);
                            String nameBus = ((JSONObject)objectTransitDetail.get("line")).getString("name");
                            String stringInstructions = "Bắt xe "+ nameBus+ ". " +parentInstructions;
                            instructionsMove.add(stringInstructions);

                            //Handle to get code bus from name of bus, sample: "19 - Ben Thanh - Suoi Tien"
                            String patternCodeBus = "^\\d+";
                            Pattern r = Pattern.compile(patternCodeBus);
                            Matcher matcher = r.matcher(nameBus);
                            if(matcher.find()){
                                String codeBus = matcher.group(0);
                                codeRouteBus.add(codeBus);
                            }
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException JE){
            JE.printStackTrace();
        } catch (Exception E) {
            E.printStackTrace();
        }

        return routes;
    }

    public ArrayList<String> getNameBusStop(){
        return nameBusStop;
    }

    public ArrayList<LatLng> getListBusStop(){
        return listBusStop;
    }

    public ArrayList<String> getInstructionsMove(){
        return instructionsMove;
    }

    public ArrayList<String> getCodeBus(){
        return codeRouteBus;
    }

    public HashMap<String, String> getInfoRoute(){

        HashMap<String, String> allInfo = new HashMap<>();
        allInfo.put("distance", distanceTransit);
        allInfo.put("duration", durationTransit);
        allInfo.put("start_address", startAddress);
        allInfo.put("end_address", endAddress);
        allInfo.put("arrival_time", arrivalTime);
        allInfo.put("departure_time", departureTime);
        allInfo.put("status",status);

        return allInfo;
    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}
