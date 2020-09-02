package com.example.admybin.admybin;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity implements OnQueryTextListener {

    private ArrayList<Location> locationList = new ArrayList<>();

    private RecyclerView recyclerView;
    private LocationAdapter mAdapter;


    JSONParser jsonParser = new JSONParser();

    private static final String Bin_URL = "http://www.admybin.com/intern_test/bin_info_app.php";

    private static final String Bin_Loction = "location";


    private static String bin_loc;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new bin_info(MainActivity.this).execute();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new LocationAdapter(locationList);
        //recyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<Location> newlocationList = new ArrayList<>();

        for (Location location : locationList) {

            String locationName = location.getLocation().toLowerCase();
            if (locationName.contains(newText)) {
                newlocationList.add(location);
            }
        }
        mAdapter.setFilter(newlocationList);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        return true;
    }





    private class bin_info extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog;
        private AppCompatActivity activity;

        public bin_info(AppCompatActivity activity) {
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

            if (new CheckNetwork(MainActivity.this).isNetworkAvailable()) {

                Log.d("request!", "starting");
                JSONArray json1 = jsonParser.getJSONFromUrlArr(Bin_URL);


                for (int i = 0; i < json1.length(); i++) {


                    try {
                        JSONObject c1 = json1.getJSONObject(i);

                        bin_loc = c1.getString(Bin_Loction);

                        //System.out.println("Bin name"+bin_name);

                        Location location = new Location();

                        location.setLocation(bin_loc);
                        locationList.add(location);

                        final HashSet<String> years = new HashSet<String>(locationList.size());
                        final Iterator<Location> iter = locationList.iterator();
                        while (iter.hasNext()) {
                            final Location award = iter.next();
                            final String year = award.getLocation();
                            if (years.contains(year)) {
                                iter.remove();
                            } else {
                                years.add(year);
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        // Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Error");
                        builder.setMessage("No internet connection.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
            return null;
        }

        protected void onPostExecute(String message) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
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


}
