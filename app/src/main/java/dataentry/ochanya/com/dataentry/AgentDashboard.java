package dataentry.ochanya.com.dataentry;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AgentDashboard extends AppCompatActivity {
    private Button schl_btn, mkt_btn, rpt_btn, syncBtn, logout;
    private TextView welcomeMSG;
    private String uid, role, designation, desig_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_dashboard);

        //get all intent values
        Intent in=getIntent();
        uid=in.getStringExtra("uid");
        role=in.getStringExtra("role");
        designation=in.getStringExtra("des");
        desig_id=in.getStringExtra("did");

        //set app title
        //getSupportActionBar().setTitle("m");

        schl_btn=(Button) findViewById(R.id.ad_reg_school);
        mkt_btn=(Button) findViewById(R.id.ad_reg_market);
        rpt_btn=(Button) findViewById(R.id.ad_report);
        syncBtn=(Button) findViewById(R.id.ad_syncBtn);
        welcomeMSG=(TextView) findViewById(R.id.ad_welcome);
        logout=(Button) findViewById(R.id.ad_logout);

        //Check designation to display
        if(designation.equalsIgnoreCase("market")){
            schl_btn.setVisibility(View.GONE);
        }else if(designation.equalsIgnoreCase("school")){
            mkt_btn.setVisibility(View.GONE);
        }else{
            schl_btn.setVisibility(View.GONE);
            mkt_btn.setVisibility(View.GONE);
        }

        //Set listener for market registration button
        mkt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in2=new Intent(AgentDashboard.this, MarketRegistration.class);
                in2.putExtra("uid", uid);
                in2.putExtra("role", role);
                in2.putExtra("des", designation);
                in2.putExtra("did", desig_id);
                startActivity(in2);
            }
        });

        //Set listener for school registration button
        schl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in2=new Intent(AgentDashboard.this, SchoolRegistration.class);
                in2.putExtra("uid", uid);
                in2.putExtra("role", role);
                in2.putExtra("des", designation);
                in2.putExtra("did", desig_id);
                startActivity(in2);
            }
        });

        //Set onclick listener for logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder backBuilder=new AlertDialog.Builder(AgentDashboard.this);
                backBuilder.setTitle("Warning");
                backBuilder.setMessage("Do you want to logout?");
                backBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent logoutIn=new Intent(AgentDashboard.this, User_Login.class);
                        startActivity(logoutIn);
                        finish();
                    }
                });
                backBuilder.setNegativeButton("No", null);
                backBuilder.show();
            }
        });

        //Set onclick listener for synchronization button
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checknetwork=checkNetwork();
                if(checknetwork){
                    Toast.makeText(AgentDashboard.this, "Synchronizing...", Toast.LENGTH_LONG).show();
                    //ConfirmAgentPermit("https://royalsophen.com/api/confirm_agent.php");
                    MobileAsyn ma=new MobileAsyn(AgentDashboard.this, uid);
                    ma.execute();
                }else{
                    Toast.makeText(AgentDashboard.this, "No internet connection for synchronization!", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Set onclick listener for report button
        rpt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(AgentDashboard.this,SimpleReport.class);
                in.putExtra("uid", uid);
                in.putExtra("role", role);
                in.putExtra("des", designation);
                in.putExtra("did", desig_id);
                startActivity(in);
            }
        });
    }

    //Check network availability
    public boolean checkNetwork(){
        ConnectivityManager connectivityManager;
        connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
    private void ConfirmAgentPermit(final String urlWebService) {
        class DownloadJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    PerformAction(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    //Set values for post
                    String data  = URLEncoder.encode("uid", "UTF-8") + "=" +
                            URLEncoder.encode(uid, "UTF-8");

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

    //Perform action after checking if agent is permitted to synchronize data
    private void PerformAction(String json) throws JSONException {
        JSONObject jobj=new JSONObject(json);
        String res =jobj.getString("res").toString().trim();

        if(res.equalsIgnoreCase("true")){
            MobileAsyn ma=new MobileAsyn(AgentDashboard.this, uid);
            ma.execute();
        }else{
            //Toast.makeText(AgentDashboard.this, "Data Synchronization failed due to improper permission. Contact administrator if you think this is an error!", Toast.LENGTH_LONG).show();
            AlertDialog.Builder error_alert=new AlertDialog.Builder(AgentDashboard.this);
            error_alert.setTitle("Synchronization Failed");
            error_alert.setMessage("Data Synchronization failed due to not enough permission. Contact administrator if you think this is an error!");
        }

    }**/

    @Override
    public void onBackPressed() {
        AlertDialog.Builder backBuilder=new AlertDialog.Builder(AgentDashboard.this);
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
