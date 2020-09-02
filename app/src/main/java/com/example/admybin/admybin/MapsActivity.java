package com.example.admybin.admybin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    View view;
    private String img;

    EditText edtSearch;
    LinearLayout search;
    ImageButton search_icon;


    private ArrayList<com.example.admybin.admybin.Location> locationList = new ArrayList<>();
    private ArrayList<com.example.admybin.admybin.Location> latitudeList = new ArrayList<>();
    private ArrayList<com.example.admybin.admybin.Location> longitudeList = new ArrayList<>();

    private ArrayList<BinDetails> binList = new ArrayList<>();

    JSONParser jsonParser = new JSONParser();
    private static final String Bin_URL = "http://www.admybin.com/intern_test/bin_info_app.php";

    private static final String Bin_BinId = "bin_id";
    private static final String Bin_BinName = "bin_name";
    private static final String Bin_BinImeiNo = "imei_no";
    private static final String Bin_Type = "type";
    private static final String Bin_Latitude = "latitude";
    private static final String Bin_Longitude = "longitude";
    private static final String Bin_Loction = "location";
    private static final String Bin_Address = "address";
    private static final String Bin_BinCode = "bin_code";

    private static int bin_id;
    private static String bin_name;
    private static String bin_imei;
    private static String bin_type;
    private static double bin_lat;
    private static double bin_lon;
    private static String bin_loc;
    private static String bin_add;
    private static String bin_code;

    Context context;


    private static final String BIN_LEVEL_URL = "http://www.admybin.com/intern_test/bin_level_data_app.php";

    private static final String BIN_CODE = "bin_code";
    private static final String BIN_VIEW_COUNT = "bin_view_count";
    private static final String BIN_LEVEL_COUNT = "bin_level_count";


    private static String bincode;
    private static int bin_view;
    private static int bin_level;

    private double latitude;
    private double longitude;

   // private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        /*dialog= new ProgressDialog (MapsActivity.this);
        dialog.setMessage("Loading..");
*/
        new bin_info_map().execute();


        search = (LinearLayout) findViewById(R.id.search);
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        search_icon= (ImageButton) findViewById(R.id.imgSearch);


        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    onSearch(view);
                    return true;
                }
                return false;
            }
        });




        //Intent in = getIntent();
        // Bundle val1 = in.getExtras();
        // latitude = val1.getDouble("latitude");
        // longitude = val1.getDouble("longitude");

        //for (int i = 0; i < locationList.size(); i++) {

        // }

       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }*/
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Make sure that GPS is enabled on the device
        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            showDialogGPS();
        }


    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setPadding(0, 200, 0, 0);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();

                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            } else {
                //Request Location Permission
                //checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }

        // LatLng pinLocation = new LatLng(latitude, longitude);
        // MarkerOptions markerOptions1 = new MarkerOptions().position(pinLocation).icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("logo", 40, 40)));
                    /*.title(locationList.get(i).getBinName())
                    .snippet(locationList.get(i).getAddress())*/

        // mMap.addMarker(markerOptions1);


        //Used for Adding bin image in maps
        /*int per=70;

        if(per<20) {
             img= "greenbin";
        }
        else{
            img="redbin";
        }
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(41.906991, 12.453360))
                    .title("Current Location")
                    .snippet("Thinking of finding some thing...")
                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons(img, 40, 40)));
            mMap.addMarker(markerOptions);*/


        //for setting the location buttons position

        /*if (view != null &&
                view.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 100, 100);
        }*/
    }


    //Used for Adding bin image in maps
    /* public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }*/


    public void onSearch(View view) {
       // EditText location_tf = (EditText) findViewById(R.id.edtSearch);
        String location = edtSearch.getText().toString();
        List<android.location.Address> addressList = null;

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // edtSearch.setText("");


            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
                if (addressList.size()>0) {


            android.location.Address address = addressList.get(0);
            String locality = address.getLocality();
            Toast.makeText(this, locality, Toast.LENGTH_LONG).show();
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                    edtSearch.setText("");

        } else {
            Toast.makeText(this, "Please enter a proper location.", Toast.LENGTH_LONG).show();
        }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    /*public void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();



            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            //return false;
        } *//*else {
            return true;
        }*//*
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();

                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    /**
     * Show a dialog to the user requesting that GPS be enabled
     */
    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Enable GPS");
        builder.setMessage("Please enable GPS");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private class bin_info_map extends AsyncTask<String, Void, String> {






        @Override
        protected void onPreExecute() {

           // dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {


            Log.d("request!", "starting");


            try {
                JSONArray json = jsonParser.getJSONFromUrlArr(BIN_LEVEL_URL);

                for (int i = 0; i < json.length(); i++) {
                    JSONObject c = json.getJSONObject(i);

                    // System.out.println("list" + json.length());

                    bincode = c.getString(BIN_CODE);
                    bin_view = c.getInt(BIN_VIEW_COUNT);
                    bin_level = c.getInt(BIN_LEVEL_COUNT);
                    //System.out.println("IMEI NO" + imeio_no);
                    //System.out.println("bin level" + bin_level);
                    //System.out.println("bin view" + bin_view);


                    BinDetails bin = new BinDetails();
                    bin.setBincode(bincode);
                    bin.setLevel(bin_level);
                    bin.setPeople(bin_view);
                    binList.add(bin);

                    // System.out.println("binlist" + binList);
                }


                JSONArray json1 = jsonParser.getJSONFromUrlArr(Bin_URL);


                for (int i = 0; i < json1.length(); i++) {

                    JSONObject c1 = json1.getJSONObject(i);

                    bin_id = c1.getInt(Bin_BinId);
                    bin_code = c1.getString(Bin_BinCode);
                    bin_name = c1.getString(Bin_BinName);
                    bin_imei = c1.getString(Bin_BinImeiNo);
                    bin_type = c1.getString(Bin_Type);
                    bin_lat = c1.getDouble(Bin_Latitude);
                    bin_lon = c1.getDouble(Bin_Longitude);
                    bin_loc = c1.getString(Bin_Loction);
                    bin_add = c1.getString(Bin_Address);

                    //System.out.println("Bin name  " + bin_name + " IMEI NO " + bin_lon + " " + bin_lat);

                    com.example.admybin.admybin.Location location1 = new com.example.admybin.admybin.Location();
                    com.example.admybin.admybin.Location latitude = new com.example.admybin.admybin.Location();
                    com.example.admybin.admybin.Location longitude = new com.example.admybin.admybin.Location();

                    location1.setBinID(bin_id);
                    location1.setBinCode(bin_code);
                    location1.setBinName(bin_name);
                    // System.out.println("CHECKING, NEEDED " + bin_name);
                    location1.setImei(bin_imei);
                    location1.setType(bin_type);
                    location1.setLatitude(bin_lat);
                    location1.setLongitude(bin_lon);
                    location1.setLocation(bin_loc);
                    location1.setAddress(bin_add);
                    locationList.add(location1);

                    latitude.setLatitude(bin_lat);
                    latitudeList.add(latitude);

                    longitude.setLatitude(bin_lat);
                    longitudeList.add(longitude);
                }




                       /* MapsActivity.this.runOnUiThread(new Runnable() {
                            public void run() {


                            }
                        });*/


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostExecute(String message) {



            final ArrayList<String> tempbincode = new ArrayList<>();
            for (int i = 0; i < locationList.size(); i++) {

                for (int z = 0; z < binList.size(); z++) {
                    tempbincode.add(binList.get(z).getBincode());
                }

                if (tempbincode.contains(locationList.get(i).getBinCode())) {

                    int z = tempbincode.indexOf((locationList.get(i).getBinCode()));

                    for (int j = 0; j < binList.size(); j++) {

                        int per = binList.get(z).getLevel();

                        if (per > 0 && per <= 30) {

                            LatLng pinLocation = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(pinLocation)
                                    .title(locationList.get(i).getBinName().substring(0, 1).toUpperCase() + locationList.get(i).getBinName().substring(1))
                                    .snippet(locationList.get(i).getAddress().substring(0, 1).toUpperCase() + locationList.get(i).getAddress().substring(1)+"."+"\n"+"This Bin is "+ per+"% filled.")
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("greenbin", 50, 50)));
                            mMap.addMarker(markerOptions);
                        } else if (per > 30 && per <= 70) {

                            LatLng pinLocation = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(pinLocation)
                                    .title(locationList.get(i).getBinName().substring(0, 1).toUpperCase() + locationList.get(i).getBinName().substring(1))
                                    .snippet(locationList.get(i).getAddress().substring(0, 1).toUpperCase() + locationList.get(i).getAddress().substring(1)+"."+"\n"+"This Bin is "+ per+"% filled.")
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("yellowbin", 50, 50)));
                            mMap.addMarker(markerOptions);

                        } else if (per > 70) {

                            LatLng pinLocation = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(pinLocation)
                                    .title(locationList.get(i).getBinName().substring(0, 1).toUpperCase() + locationList.get(i).getBinName().substring(1))
                                    .snippet(locationList.get(i).getAddress().substring(0, 1).toUpperCase() + locationList.get(i).getAddress().substring(1)+"."+"\n"+"This Bin is "+ per+"% filled.")
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("redbin", 50, 50)));
                            mMap.addMarker(markerOptions);
                        } else {

                            LatLng pinLocation = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(pinLocation)
                                    .title(locationList.get(i).getBinName().substring(0, 1).toUpperCase() + locationList.get(i).getBinName().substring(1))
                                    .snippet(locationList.get(i).getAddress().substring(0, 1).toUpperCase() + locationList.get(i).getAddress().substring(1)+"."+"\n"+"This Bin has not been used yet.")
                                    .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("logo", 50, 50)));
                            mMap.addMarker(markerOptions);
                        }

                    }
                }else
                {
                    LatLng pinLocation = new LatLng(locationList.get(i).getLatitude(), locationList.get(i).getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(pinLocation)
                            .title(locationList.get(i).getBinName().substring(0, 1).toUpperCase() + locationList.get(i).getBinName().substring(1))
                            .snippet(locationList.get(i).getAddress().substring(0, 1).toUpperCase() + locationList.get(i).getAddress().substring(1)+"."+"\n"+"This Bin is a Non-technical bin.")
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("logo", 50, 50)));
                    mMap.addMarker(markerOptions);
                }


            }
            MapsActivity.this.runOnUiThread(new Runnable() {
                public void run() {


                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

                            LinearLayout info = new LinearLayout(MapsActivity.this);
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(MapsActivity.this);
                            title.setTextColor(Color.BLACK);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(MapsActivity.this);
                            snippet.setTextColor(Color.GRAY);
                            snippet.setGravity(Gravity.CENTER);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });
                }
            });

            /*if (dialog.isShowing()) {
                dialog.dismiss();
            }*/


        }
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

}
