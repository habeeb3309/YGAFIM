package com.example.woody30;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private HashMap<String,String> parserJsonObject(JSONObject object){

        HashMap<String, String> dataList = new HashMap<>();//Initalising hashmap
        try {
            //get name
            String name = object.getString("name");

            //get the latitude from object
            String latitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");

            //then get longitude
            String longitude = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");

            //put values in hashmap
            dataList.put("name", name);
            dataList.put("lat", latitude);
            dataList.put("lng", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return the hash map
        return dataList;
    }

    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        //Hashmap list
        List<HashMap<String,String>> dataList =  new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++){
            //Hashmap
            try {
                HashMap<String,String> data =  parserJsonObject((JSONObject) jsonArray.get(i));
                //add data in hash map list
                dataList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //return hashmap
        return dataList;
    }

    public List<HashMap<String, String>> parseResult(JSONObject object){
        //Initalise json array
        JSONArray jsonArray = null;

        //get results
        try {
            jsonArray = object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseJsonArray(jsonArray);
    }
}
