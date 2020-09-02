package com.example.admybin.admybin;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUserName;
    private EditText editTextPassword;
    private Button login_btn;

    String username;
    String password;

    JSONParser jsonParser = new JSONParser();

    private static final String LOGIN_URL = "http://admybin.com/intern_test/login_backend.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUserName = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*Intent intent = new Intent(LoginActivity.this, Tab.class);
                startActivity(intent);*/
                username = editTextUserName.getText().toString();
                password = editTextPassword.getText().toString();


                new login().execute();

            }
        });
    }


    private class login extends AsyncTask<String, Void, String> {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;

            if (new CheckNetwork(LoginActivity.this).isNetworkAvailable()) {



            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST", params);

                // checking log for json response
                Log.d("Login attempt", json.toString());
                // success tag for json
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("username", username);
                    editor.commit();

                    Log.d("Successfully Login!", json.toString());
                    Intent intent = new Intent(LoginActivity.this, Tab.class);
                    finish();
                    startActivity(intent);
                    return json.getString(TAG_MESSAGE);
                } else {
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            else{

                LoginActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                       // Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(LoginActivity.this);
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

            /*try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost("http://admybin.com/intern_test/login_backend.php");

                String query = "";
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(username, params[0]);
                jsonObject.put(password, params[1]);
                query = jsonObject.toString();
                System.out.println(query);

                StringEntity se = new StringEntity(query);
                httppost.setEntity(se);

                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                //YOUR PHP SCRIPT ADDRESS
                HttpResponse response = httpclient.execute(httppost);

                isr = response.getEntity().getContent();

                if (isr != null)
                    result = convertInputStreamToString(isr);
                else
                    result = "Did not work!";
                System.out.println("ASYNC TASK METHOD : " + result);

            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
                Log.d("InputStream", e.getLocalizedMessage());

            }
            return result;*/


        }

        @Override
        protected void onPostExecute(String message) {

            if (message != null) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }

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

}



