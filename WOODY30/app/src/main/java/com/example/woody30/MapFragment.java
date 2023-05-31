package com.example.woody30;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLong = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        spType = view.findViewById(R.id.sp_type);
        btFind = view.findViewById(R.id.bt_find);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //array of place type
        String[] placeTypeList = {"hospital", "gp", "doctor","pharmacy"};

        //array place name
        String[] placeNameList = {"Hospital", "GP", "Doctor", "Pharmacy"};

        //Setting the adapter on the spinner
        spType.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, placeNameList));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        //permission
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //when permission granted
            //call method
            getCurrentLocation();
        } else {
            //when permission denied
            //Request permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

        }

        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get selected position of spinner
                int i = spType.getSelectedItemPosition();
                //
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + //url
                        "?location=" + currentLat + "," + currentLong + //Location latitude and longitude
                        "&radius=5000" + // Nearby Radius of 5000
                        "&types=" + placeTypeList[i] +//place type
                        "&sensor=true" + //Sensor
                        "&key=" + getResources().getString(R.string.google_map_key);//map key

                //Execute place task method to download json data
                new PlaceTask().execute(url);

            }
        });

        return view;
    }

    private void getCurrentLocation() {

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {

                    //get current latitude
                    currentLat = location.getLatitude();
                    //get current longitude
                    currentLong = location.getLongitude();
                    //Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            //When map is ready
                            map = googleMap;
                            //Zoom Current Location
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(

                                    new LatLng(currentLat, currentLong), 10
                            ));
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //When permission granted
                //Call Method
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

    }

    private class PlaceTask extends AsyncTask<String, Integer, String> {

        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new MapFragment.ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {

        URL url = new URL(string);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.connect();

        InputStream stream = connection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder = new StringBuilder();

        String line = "";

        while ((line = reader.readLine()) != null) {

            builder.append(line);
        }
        String data = builder.toString();

        reader.close();

        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {

            JsonParser jsonParser = new JsonParser();
            //hash map list
            List<HashMap<String, String>> mapList = null;
            JSONObject object = null;
            //json object
            try {
                //
                object = new JSONObject(strings[0]);
                //parse json
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //return map list

            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //Clear map
            if (map != null) { // Check if GoogleMap is not null
                map.clear();

                //use for loop
                for (int i = 0; i < hashMaps.size(); i++) {

                    HashMap<String, String> hashMapList = hashMaps.get(i);
                    //Get latitude
                    double lat = Double.parseDouble(hashMapList.get("lat"));
                    //get longitude
                    double lng = Double.parseDouble(hashMapList.get("lng"));
                    //get name
                    String name = hashMapList.get("name");
                    //Concat latitude and longitude
                    LatLng latLng = new LatLng(lat, lng);
                    //marker options
                    MarkerOptions options = new MarkerOptions();
                    //set position
                    options.position(latLng);
                    //title
                    options.title(name);
                    //Add Marker on map
                    map.addMarker(options);


                }
            }
        }
    }
}
