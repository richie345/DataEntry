package dataentry.ochanya.com.dataentry;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class AgentDashboard extends AppCompatActivity {
    private Button schl_btn, mkt_btn, rpt_btn, syncBtn, msgBtn, logout;
    private TextView welcomeMSG, total, today;
    private String uid, role, designation, desig_id;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_dashboard);

        openHelper=new SQLDBHelper(AgentDashboard.this);
        db=openHelper.getReadableDatabase();

        //get all intent values
        Intent in=getIntent();
        uid=in.getStringExtra("uid");
        role=in.getStringExtra("role");
        designation=in.getStringExtra("des");
        desig_id=in.getStringExtra("did");

        //calling schedule
        scheduleAlarm(uid);

        int total_count = 0;
        int today_count = 0;
        total = (TextView) findViewById(R.id.total);
        today = (TextView) findViewById(R.id.today);

        Calendar now = Calendar.getInstance();
        //String currentdate = now.get(Calendar.YEAR) + "-"
               // + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE);
        String currentdate = currDate();
        String[] args={
                uid,
                currentdate
        };
        Cursor cursor=db.rawQuery(String.format("select * from %s where agent_id=? and reg_date=?", SQLDBHelper.USER_TABLE),args);
        int cursor_count=cursor.getCount();
        if(cursor_count>0) {
            while (cursor.moveToNext()) {
                today_count++;
            }
        }
        today.setText(""+today_count);
        String[] args1={
                uid
        };
        Cursor c=db.rawQuery(String.format("select * from %s where agent_id=?", SQLDBHelper.USER_TABLE),args1);
        int c_count=c.getCount();
        if(c_count>0) {
            while (c.moveToNext()) {
                total_count++;
            }
        }
        total.setText(""+total_count);
        //set app title
        //getSupportActionBar().setTitle("m");

        schl_btn=(Button) findViewById(R.id.ad_reg_school);
        mkt_btn=(Button) findViewById(R.id.ad_reg_market);
        rpt_btn=(Button) findViewById(R.id.ad_report);
        syncBtn=(Button) findViewById(R.id.ad_syncBtn);
        welcomeMSG=(TextView) findViewById(R.id.ad_welcome);
        msgBtn = (Button) findViewById(R.id.ad_msgBtn);
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

        //Set onclick for msg button
        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in2=new Intent(AgentDashboard.this, SendMessage.class);
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

    public String currDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void scheduleAlarm(String uid)
    {
        // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
        // we fetch  the current time in milliseconds and added 1 day time
        // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day
        Long time = new GregorianCalendar().getTimeInMillis() + 60*60*1000;

        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we call the method inside onRecieve() method pf Alarmreciever class
        Intent intentAlarm = new Intent(this, AlarmReciever.class);
        intentAlarm.putExtra("uid", uid);
        sendBroadcast(intentAlarm);

        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Log.i("Alarm Check: ","Alarm Scheduled");

    }

}
