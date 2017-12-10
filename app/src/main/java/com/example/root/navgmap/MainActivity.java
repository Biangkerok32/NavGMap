package com.example.root.navgmap;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.example.root.navgmap.R.id.url;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GoogleMap nMap;
    MapFragment mapMessageLocation;
    String placeName;
    double latitude, longitude;
    double endlatitude, endlongitude;
    private FusedLocationProviderClient mFusedLocationClient;
    int PROXIMITY_RADIUS = 500;
    GetDirectionsData getDirectionsData;
    private static final int REQUEST_FINE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getDirectionsData = new GetDirectionsData(getApplicationContext());



        loadPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION,REQUEST_FINE_LOCATION);

        if (mapMessageLocation == null) {
            mapMessageLocation = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        }

        mapMessageLocation.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                nMap = map;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                        (MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                nMap.setMyLocationEnabled(true);
            }
        });

        final PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        places.setHint("Search Destination point");
        places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                nMap.clear();
                placeName = place.getName().toString();
                LatLng latLng = place.getLatLng();

                endlatitude = latLng.latitude;
                endlongitude = latLng.longitude;

                nMap.addMarker(new MarkerOptions().position(latLng).title(placeName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                nMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                nMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));




                Toast.makeText(MainActivity.this,place.getName(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Status status) {

                Toast.makeText(MainActivity.this,status.toString(),Toast.LENGTH_LONG).show();

            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {

                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(latitude,longitude));
                            markerOptions.title("It is my location");
                            nMap.addMarker(markerOptions);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            nMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                            nMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude)));

                        }
                    }
                });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    private void loadPermissions(String perm,int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm},requestCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // granted
                }
                else{
                    // no granted
                }
                return;
            }

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mapTypeNormal:
                nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                nMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                nMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                nMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            default:
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Object datatransfer[] = new Object[2];
        GetNearbyPlaceData getNearbyPlaceData = new GetNearbyPlaceData(getApplicationContext());
        String url;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_hospital) {
            nMap.clear();
            String hospitals = "hospitals";
            url = geturl(latitude, longitude, hospitals);
            datatransfer[0] = nMap;
            datatransfer[1] = url;
            getNearbyPlaceData.execute(datatransfer);
            Toast.makeText(MainActivity.this, "Showing Nearby Hospitals", Toast.LENGTH_LONG).show();
            // Handle the camera action
        } else if (id == R.id.nav_school) {
            nMap.clear();
            String school = "school";
            url = geturl(latitude, longitude, school);
            datatransfer[0] = nMap;
            datatransfer[1] = url;
            getNearbyPlaceData.execute(datatransfer);
            Toast.makeText(MainActivity.this, "Showing Nearby Schools", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_restaurant) {
            nMap.clear();
            String restaurent = "restaurent";
            url = geturl(latitude, longitude, restaurent);
            datatransfer[0] = nMap;
            datatransfer[1] = url;
            getNearbyPlaceData.execute(datatransfer);
            Toast.makeText(MainActivity.this, "Showing Nearby restaurants", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_to) {

            if(endlatitude == 0){
                Toast.makeText(MainActivity.this,"please write the destination point",
                        Toast.LENGTH_LONG).show();

            }

            else {

                nMap.clear();
                datatransfer = new Object[3];
                url = getdicetionUrl();

                datatransfer[0] = nMap;
                datatransfer[1] = url;
                datatransfer[2] = new LatLng(endlatitude, endlongitude);

                if (getDirectionsData.getStatus() == AsyncTask.Status.FINISHED) {
                    getDirectionsData = new GetDirectionsData(getApplicationContext());
                }

                if (getDirectionsData.getStatus() != AsyncTask.Status.RUNNING) {
                    getDirectionsData.execute(datatransfer);
                }

            }






        } else if (id == R.id.nav_details) {

            if(getDirectionsData.distance == null){
                Toast.makeText(MainActivity.this,"First click to the Distance" +
                                " and Duration then click Ride Details",
                        Toast.LENGTH_LONG).show();
            }

            else {
                nMap.clear();
                Intent i = new Intent(MainActivity.this, Details.class);
                i.putExtra("distancetotal",getDirectionsData.distance);
                i.putExtra("durationtotal",getDirectionsData.duration);
                i.putExtra("minutes",getDirectionsData.minute);
                i.putExtra("endlocation",placeName);
                startActivity(i);
            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private String getdicetionUrl() {

        StringBuilder googleDirectionurl = new StringBuilder("https://maps.googleapis.com/maps" +
                "/api/directions/json?");
        googleDirectionurl.append("origin=" + latitude + "," + longitude);
        googleDirectionurl.append("&destination=" + endlatitude + "," + endlongitude);
        googleDirectionurl.append("&key=" + "AIzaSyDd0-e75Vhyq18rSdp4Bx18iuZBwcfrXjc");
        return googleDirectionurl.toString();
    }

    private String geturl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googleplaceurl = new StringBuilder("https://maps." +
                "googleapis.com/maps/api/place/nearbysearch/json?");
        googleplaceurl.append("location=" + latitude + "," + longitude);
        googleplaceurl.append("&radius=" + PROXIMITY_RADIUS);
        googleplaceurl.append("&type=" + nearbyPlace);
        googleplaceurl.append("&key=" + "AIzaSyBCpnsqxJIFcipVragrgJwoNGcv2PQAMmw");
        return googleplaceurl.toString();
    }


}
