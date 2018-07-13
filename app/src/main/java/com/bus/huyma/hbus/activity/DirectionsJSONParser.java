package com.bus.huyma.hbus.activity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser  {

    private String distanceTransit,durationTransit,travel_mode;
//    private ArrayList<String> instructionsMove = new ArrayList<>();
    private StringBuilder move = new StringBuilder();

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {
            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    String distance="", duration="";
                    JSONObject JsonObjectLegs = ((JSONObject) jLegs.get(j));
                    jSteps = JsonObjectLegs.getJSONArray("steps");
                    distance = ((JSONObject)JsonObjectLegs.get("distance")).getString("text");
                    duration = ((JSONObject)JsonObjectLegs.get("duration")).getString("text");

                    //Assign all info data for origin init variable
                    distanceTransit = distance;
                    durationTransit = duration;

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        String parentInstructions;
                        JSONObject jsonObjectStep = ((JSONObject)jSteps.get(k));
                        parentInstructions = jsonObjectStep.getString("html_instructions");
                        polyline = ((JSONObject)jsonObjectStep.get("polyline")).getString("points");
                        travel_mode = (((JSONObject) jSteps.get(k)).getString("travel_mode"));
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                        if(travel_mode.equals("WALKING")){
//                            instructionsMove.add(parentInstructions);
                            move.append(parentInstructions+"\n");
                        }
                    }
                    routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return routes;
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

    public StringBuilder getInstructionsWalking(){
        return move;
    }

    public HashMap<String, String> getInfoWalking(){

        HashMap<String, String> allInfo = new HashMap<>();
        allInfo.put("distance", distanceTransit);
        allInfo.put("duration", durationTransit);

        return allInfo;
    }
}
