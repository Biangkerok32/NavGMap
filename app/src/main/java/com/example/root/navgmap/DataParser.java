package com.example.root.navgmap;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by root on 8/27/17.
 */

public class DataParser {
    private HashMap<String, String> getpalce(JSONObject googleplacejson) {
        HashMap<String, String> googleplacemap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longtitude = "";
        String reference = "";


        try {
            if (googleplacejson.isNull("name") == false) {
                placeName = googleplacejson.getString("name");
            }
            if (googleplacejson.isNull("vicinity") == false) {
                vicinity = googleplacejson.getString("vicinity");
            }
            if (googleplacejson.isNull("geometry") == false) {
                latitude = googleplacejson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longtitude = googleplacejson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            }
            if (googleplacejson.isNull("reference") == false) {
                reference = googleplacejson.getString("reference");
            }

            googleplacemap.put("PlaceName", placeName);
            googleplacemap.put("Vicinity", vicinity);
            googleplacemap.put("Latitude", latitude);
            googleplacemap.put("Longitude", longtitude);
            googleplacemap.put("Reference", reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googleplacemap;

    }

    private HashMap<String, String> getDuration(JSONArray googleDirectionjson) {
        HashMap<String, String> googleDirectionsMap = new HashMap<>();
        double duration = 0;
        double distance = 0;
        Log.d("json response", googleDirectionjson.toString());

        try {

            for (int index = 0; index < googleDirectionjson.length(); index++) {
                distance += googleDirectionjson.getJSONObject(index).getJSONObject("distance").getDouble("value");
                duration += googleDirectionjson.getJSONObject(index).getJSONObject("duration").getDouble("value");
            }

            int input = (int) duration;
            int hours = input / 3600;
            int minutes = (input % 3600) / 60;
            int seconds = (input % 3600) % 60;
            String result = " " + hours + " Hours " + minutes + " minutes " + seconds + " seconds";

            String distanceInKm = String.valueOf(distance / 1000);


            Float litersOfPetrol1 = Float.parseFloat(distanceInKm);
            DecimalFormat df1 = new DecimalFormat("0.00");
            df1.setMaximumFractionDigits(2);
            distanceInKm = df1.format(litersOfPetrol1);


            googleDirectionsMap.put("duration", result);
            googleDirectionsMap.put("minute", String.valueOf(minutes));
            googleDirectionsMap.put("distance", distanceInKm);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googleDirectionsMap;
    }


    private List<HashMap<String, String>> getplaces(JSONArray jsonArray) {
        int count = jsonArray.length();
        List<HashMap<String, String>> placeList = new ArrayList<>();
        HashMap<String, String> placeMap = null;
        for (int i = 0; i < count; i++) {
            try {
                placeMap = getpalce((JSONObject) jsonArray.get(i));
                placeList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placeList;
    }

    List<HashMap<String, String>> parse(String jsondata) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {
            Log.d("places", "parse");
            jsonObject = new JSONObject(jsondata);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getplaces(jsonArray);
    }

    public HashMap<String, String> parseDirections(String jsondata) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {

            jsonObject = new JSONObject(jsondata);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").
                    getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getDuration(jsonArray);

    }

    public String[] parsePolyline(String jsondata) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        try {

            jsonObject = new JSONObject(jsondata);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").
                    getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getpaths(jsonArray);

    }

    public String[] getpaths(JSONArray googleStepsJson) {
        int count = googleStepsJson.length();
        String[] polylines = new String[count];
        for (int i = 0; i < count; i++) {
            try {
                polylines[i] = getpath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return polylines;
    }

    public String getpath(JSONObject googlepathjson) {
        String polyline = "";
        try {
            polyline = googlepathjson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }


}



