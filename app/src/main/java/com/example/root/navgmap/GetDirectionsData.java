package com.example.root.navgmap;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by root on 8/28/17.
 */

public class GetDirectionsData extends AsyncTask<Object,String,String> {
    String googleplaceDirectionData;
    GoogleMap googleMap;
    String url;
    String duration, distance,minute;
    LatLng latLng;
    Context mContext;


    public GetDirectionsData(Context context){
        mContext = context;
    }

    @Override
    protected String doInBackground(Object... objects) {

        googleMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleplaceDirectionData = downloadUrl.readurl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleplaceDirectionData;

    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String,String> directionList = null;
        DataParser parser = new DataParser();
        directionList = parser.parseDirections(s);
        duration = directionList.get("duration");
        distance = directionList.get("distance");
        minute = directionList.get("minute");
        googleMap.clear();



        Toast.makeText(mContext, "Duration is:  " +  duration
                , Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "Distance is:  " + distance +" KM", Toast.LENGTH_LONG).show();






         String[] directionpath;
        DataParser parser1 = new DataParser();
        directionpath = parser1.parsePolyline(s);
        displaydirections(directionpath);



    }

    private void displaydirections(String[] directionsList) {
        int count = directionsList.length;
        for (int i = 0; i < count; i++) {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
          options.addAll(PolyUtil.decode(directionsList[i]));
            googleMap.addPolyline(options);


        }

    }
}