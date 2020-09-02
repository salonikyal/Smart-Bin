package com.example.admybin.admybin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Bin extends AppCompatActivity {

    private ArrayList<BinDetails> binList = new ArrayList<>();
    private ArrayList<Location> locationList1 = new ArrayList<>();
    private ArrayList<Location> templocationList1 = new ArrayList<>();

    private RecyclerView recyclerView;
    private BinAdapter mAdapter;
    private AlarmReceiver alarmReceiver;


    JSONParser jsonParser = new JSONParser();

    private static final String BIN_LEVEL_URL = "http://www.admybin.com/intern_test/bin_level_data_app.php";

    private static final String BIN_CODE = "bin_code";
    private static final String BIN_VIEW_COUNT = "bin_view_count";
    private static final String BIN_LEVEL_COUNT = "bin_level_count";


    private static String bincode;
    private static int bin_view;
    private static int bin_level;


    private String location;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin);



        callAsynchronousTask();

        Intent in = getIntent();
        Bundle val1 = in.getExtras();
        location = val1.getString("location");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setIcon(R.drawable.location);
        actionBar.setTitle(" " + location.substring(0, 1).toUpperCase() + location.substring(1));


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        mAdapter = new BinAdapter(binList, val1.getString("location"), locationList1);


       /* for (int i = 0; i < binList.size(); i++)
            alarmReceiver = new AlarmReceiver(binList, val1.getString("location"), i);*/

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }



    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            clearData();
                            new bin_level_data(Bin.this).execute();


                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000); //execute in every 50000 ms
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        MenuItem logout = menu.findItem(R.id.logout);
        search.setVisible(false);
        logout.setVisible(false);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.action_refresh:
                // refresh
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
                overridePendingTransition(0, 0);
                // Toast.makeText(this, "Refresh...", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class bin_level_data extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        private AppCompatActivity activity;

        public bin_level_data(AppCompatActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }


        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading..");
            this.dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            if (new CheckNetwork(Bin.this).isNetworkAvailable()) {

                JSONArray json = jsonParser.getJSONFromUrlArr(BIN_LEVEL_URL);

                try {
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

                        // System.out.println("Bin name  " + bin_name + " IMEI NO " + bin_imei);

                        Location location1 = new Location();
                        location1.setBinID(bin_id);
                        location1.setBinCode(bin_code);
                        location1.setBinName(bin_name);
                        System.out.println("CHECKING, NEEDED " + bin_name);
                        location1.setImei(bin_imei);
                        location1.setType(bin_type);
                        location1.setLatitude(bin_lat);
                        location1.setLongitude(bin_lon);
                        location1.setLocation(bin_loc);
                        location1.setAddress(bin_add);
                        locationList1.add(location1);

                        Location templocation1 = new Location();

                        if (!bin_loc.equals(location)) {

                            templocation1.setBinID(bin_id);
                            templocation1.setBinCode(bin_code);
                            templocation1.setBinName(bin_name);
                            System.out.println("CHECKING, NOT NEEDED " + bin_name);
                            templocation1.setImei(bin_imei);
                            templocation1.setType(bin_type);
                            templocation1.setLatitude(bin_lat);
                            templocation1.setLongitude(bin_lon);
                            templocation1.setLocation(bin_loc);
                            templocation1.setAddress(bin_add);
                            templocationList1.add(location1);

                        }
                        locationList1.removeAll(templocationList1);


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                Bin.this.runOnUiThread(new Runnable() {
                    public void run() {
                        // Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(Bin.this);
                        builder.setCancelable(false);
                        builder.setTitle("Error");
                        builder.setMessage("No internet connection.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        // mAdapter.setList(locationList1);
                        // mAdapter.notifyDataSetChanged();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(String message) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            //recyclerView.invalidate();
            //recyclerView.removeAllViewsInLayout();
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public class CheckNetwork {
        private Context context;

        public CheckNetwork(Context context) {
            this.context = context;
        }

        public boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager
                    .getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }

    public void clearData() {
        binList.clear();
        locationList1.clear(); //clear list
        mAdapter.notifyDataSetChanged(); //let your adapter know about the changes and reload view.


    }

}
