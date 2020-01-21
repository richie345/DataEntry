package dataentry.ochanya.com.dataentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class User_Login extends AppCompatActivity {
    private SQLiteDatabase db;
    private SQLiteOpenHelper helper;

   private Button loginBtn;
   private EditText uname, password;
    private ProgressDialog pDialog;
    private String uname_val, password_val;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__login);

        helper=new SQLDBHelper(User_Login.this);
        db=helper.getWritableDatabase();

        loginBtn = (Button) findViewById(R.id.login_button);
        uname=(EditText) findViewById(R.id.login_uname);
        password=(EditText) findViewById(R.id.login_pass);

        //when the login button is clicked
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] login_args={
                        uname.getText().toString(),
                        password.getText().toString(),
                        "active",
                        "agent"
                };
                Cursor login_cursor=db.rawQuery(String.format("select * from %s where username=? and password=? and status=? and role=?", SQLDBHelper.USER_LOGIN), login_args);
                if(login_cursor.getCount()>0){
                    login_cursor.moveToFirst(); //push cursor pointer to first record
                    Intent in2 = new Intent(User_Login.this,AgentDashboard.class);
                    in2.putExtra("uid", uname.getText().toString());
                    in2.putExtra("role", password.getText().toString());
                    in2.putExtra("des", login_cursor.getString(login_cursor.getColumnIndex("designation")));
                    in2.putExtra("did", login_cursor.getString(login_cursor.getColumnIndex("designation_id")));
                    startActivity(in2);
                    finish();
                }else{
                    AlertDialog.Builder local_ab=new AlertDialog.Builder(User_Login.this);
                    local_ab.setTitle("No Such User");
                    local_ab.setMessage("Would you like to try login remotely (internet connection is required)?!");
                    local_ab.setNegativeButton("Cancel", null);
                    local_ab.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(checkNetwork()){
                                downloadJSON("https://royalsophen.com/api/mobile_login.php");
                            }else{
                                Toast.makeText(User_Login.this, "Please turn on your INTERNET CONNECTION and try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    local_ab.show();
                }
            }
        });

    }

    private void downloadJSON(final String urlWebService) {
        class DownloadJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(User_Login.this);
                pDialog.setMessage("Loading. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    pDialog.dismiss();
                    LogUser(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    uname_val=uname.getText().toString();
                    password_val=password.getText().toString();

                    //Set values for post
                    String data  = URLEncoder.encode("uid", "UTF-8") + "=" +
                            URLEncoder.encode(uname_val, "UTF-8");
                    data += "&" + URLEncoder.encode("pass", "UTF-8") + "=" +
                            URLEncoder.encode(password_val, "UTF-8");

                    //create connection using URL site link passed when calling method
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //write data into open connection
                    con.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write( data );
                    wr.flush();

                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    con.disconnect();
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }
        }
        DownloadJSON getJSON = new DownloadJSON();
        getJSON.execute();
    }

    private void LogUser(String json) throws JSONException {
        JSONObject jobj=new JSONObject(json);
        String res_role =jobj.getString("role").toString().trim();
        String res_des = jobj.getString("des").toString().trim();
        String res_did = jobj.getString("did").toString().trim();

         if(res_role.equalsIgnoreCase("admin")){
             //redirect to admin webview activity
             Intent aInt = new Intent(User_Login.this,AdminMobileActivity.class);
             startActivity(aInt);
             //finish();
        }else if(res_role.equalsIgnoreCase("agent")){
             //sync data
             ContentValues syncValues=new ContentValues();
             syncValues.put("username", uname_val);
             syncValues.put("password", password_val);
             syncValues.put("role", "agent");
             syncValues.put("status", "active");
             syncValues.put("designation", res_des);
             syncValues.put("designation_id", res_did);

             //check if agent details is already contained in login table but inactive
             String[] checkarray={uname_val, password_val};
             Cursor checkcursor=db.rawQuery(String.format("select username from %s where username=? and password=?",SQLDBHelper.USER_LOGIN),checkarray);
             int syncRes;
             if(checkcursor.getCount()>0){
                 ContentValues cv=new ContentValues();
                 cv.put("status","active");
                 String[] whereargs={uname_val};
                 syncRes=db.update(SQLDBHelper.USER_LOGIN,cv,"username=?",whereargs);
             }else{
                 syncRes=(int) db.insert(SQLDBHelper.USER_LOGIN,null,syncValues);
             }

             if(syncRes>0) {
                 //redirect to agent dashboard
                 Intent in = new Intent(User_Login.this, AgentDashboard.class);
                 in.putExtra("uid", uname_val);
                 in.putExtra("role", res_role);
                 in.putExtra("des", res_des);
                 in.putExtra("did", res_did);
                 startActivity(in);
                 finish();
             }else{
                 AlertDialog.Builder alerter=new AlertDialog.Builder(User_Login.this);
                 alerter.setTitle("Error");
                 alerter.setMessage("An error occurred while trying to process your request, please try again later or contact your system administrator!");
                 alerter.show();
             }
        }else{
            AlertDialog.Builder alerter=new AlertDialog.Builder(User_Login.this);
            alerter.setTitle("Error");
            alerter.setMessage("Incorrect login details supplied, please check and try again!");
            alerter.show();
        }
    }

    //Check network availability
    public boolean checkNetwork(){
        ConnectivityManager connectivityManager;
        connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder backBuilder=new android.support.v7.app.AlertDialog.Builder(User_Login.this);
        backBuilder.setTitle("Warning");
        backBuilder.setMessage("Do you want to quit?");
        backBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(1);
            }
        });
        backBuilder.setNegativeButton("No", null);
        backBuilder.show();
    }
}
