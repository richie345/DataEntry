package dataentry.ochanya.com.dataentry;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ephraim on 1/13/2020.
 */

public class MobileAsyn extends AsyncTask<Void, Void, String> {
    private Context applicationContext;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private String errorhand;
    private String agent_uid;
    private ArrayList<HashMap<String, String>> sqlData;

    //constructor
    MobileAsyn(Context context, String a_uid){
        applicationContext=context;
        agent_uid=a_uid;
    }

    @Override
    protected void onPreExecute() {
        openHelper=new SQLDBHelper(applicationContext);
        db=openHelper.getWritableDatabase();
        super.onPreExecute();
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            finalAsync(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected String doInBackground(Void... voids) {
        Cursor cursor=db.rawQuery(String.format("select * from %s", SQLDBHelper.USER_TABLE), null);
        sqlData= new ArrayList<>();
        while(cursor.moveToNext()){
            HashMap<String, String> subDate= new HashMap<>();
            subDate.put("user_id", cursor.getString(cursor.getColumnIndex("user_id")));
            subDate.put("designation_id", cursor.getString(cursor.getColumnIndex("designation_id")));
            subDate.put("designation_type", cursor.getString(cursor.getColumnIndex("designation_type")));
            subDate.put("class", cursor.getString(cursor.getColumnIndex("class")));
            subDate.put("fullname", cursor.getString(cursor.getColumnIndex("fullname")));
            subDate.put("gender", cursor.getString(cursor.getColumnIndex("gender")));
            subDate.put("disability", cursor.getString(cursor.getColumnIndex("disability")));
            subDate.put("parent_guide_name", cursor.getString(cursor.getColumnIndex("parent_guide_name")));
            subDate.put("parent_guide_phone", cursor.getString(cursor.getColumnIndex("parent_guide_phone")));
            subDate.put("dob", cursor.getString(cursor.getColumnIndex("dob")));
            subDate.put("phone", cursor.getString(cursor.getColumnIndex("phone")));
            subDate.put("shop_number", cursor.getString(cursor.getColumnIndex("shop_number")));
            subDate.put("address", cursor.getString(cursor.getColumnIndex("address")));
            subDate.put("bvn", cursor.getString(cursor.getColumnIndex("bvn")));
            subDate.put("acct_name", cursor.getString(cursor.getColumnIndex("acct_name")));
            subDate.put("acct_number", cursor.getString(cursor.getColumnIndex("acct_number")));
            subDate.put("business_type", cursor.getString(cursor.getColumnIndex("business_type")));
            subDate.put("nok_name", cursor.getString(cursor.getColumnIndex("nok_name")));
            subDate.put("nok_number", cursor.getString(cursor.getColumnIndex("nok_number")));
            subDate.put("agent_id", cursor.getString(cursor.getColumnIndex("agent_id")));
            subDate.put("reg_date", cursor.getString(cursor.getColumnIndex("reg_date")));
                Log.e("about to","1");
                try {
                    byte[] blob = cursor.getBlob(cursor.getColumnIndex("user_image"));
                    if(blob != null) {
                        Log.e("blob", "2 " + blob.toString());
                        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                        Log.e("bitmap", "3 " + bitmap.toString());
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
                        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        Log.e("encodedImage ", "encoded " + encodedImage);
                        subDate.put("user_image", encodedImage);
                    }else{
                        continue;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            sqlData.add(subDate);
        }
        Gson gson=new GsonBuilder().create();
        String jsonData=gson.toJson(sqlData);
        try {
            //Set values for post
            String data  = URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(jsonData, "UTF-8");
            data+="&" + URLEncoder.encode("aid","UTF-8")+"="+URLEncoder.encode(agent_uid,"UTF-8");

            //create connection using URL site link passed when calling method
            URL url = new URL("https://royalsophen.com/api/web_sync.php");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            //write data into open connection
            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(data);
            wr.flush();

            StringBuilder sb = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String json;
            while ((json = bufferedReader.readLine()) != null) {
                sb.append(json + "\n");
            }
            errorhand=con.getResponseMessage();
            con.disconnect();
            return sb.toString().trim();
        } catch (Exception e) {
            Log.d("TAG: ", e.getMessage());
            return e.getMessage();
        }
        //return null;
    }

    //Callback function to get json feedback
    private void finalAsync(String json2) throws JSONException {
        JSONObject jobj=new JSONObject(json2);

        //Sync result
        String res =jobj.getString("res").toString().trim();

        //Agent result
        String a_status=jobj.getString("status").toString().trim();
        String a_designation=jobj.getString("designation").toString().trim();
        String a_designation_id=jobj.getString("designation_id").toString().trim();

        Gson gson2=new GsonBuilder().create();

        //users result
        JSONArray jobj_users=jobj.getJSONArray("users");

        if(!res.equalsIgnoreCase("empty")) {
            if (res.equalsIgnoreCase("true")) {
                //sync agent
                syncAgentNow(a_status, a_designation, a_designation_id);

                if (jobj_users.length()>0){
                    //loop through users detail
                    for (int i = 0; i < jobj_users.length(); i++) {
                        JSONObject jobj_users2 = jobj_users.getJSONObject(i);
                        //String test = jobj_users2.getString("fullname");
                        //check if user exist and sync
                        if (checkForUser(jobj_users2.getString("user_id"))==false) {
                            //sync user detail and save to local
                            syncUserNow(jobj_users2.getString("user_id"), jobj_users2.getString("designation_id"),
                                    jobj_users2.getString("designation_type"), jobj_users2.getString("class"),
                                    jobj_users2.getString("fullname"), jobj_users2.getString("gender"),
                                    jobj_users2.getString("disability"), jobj_users2.getString("parent_guide_name"),
                                    jobj_users2.getString("parent_guide_phone"), jobj_users2.getString("dob"),
                                    jobj_users2.getString("phone"), jobj_users2.getString("shop_number"),
                                    jobj_users2.getString("address"), jobj_users2.getString("bvn"),
                                    jobj_users2.getString("acct_name"), jobj_users2.getString("acct_number"),
                                    jobj_users2.getString("business_type"), jobj_users2.getString("nok_name"),
                                    jobj_users2.getString("nok_number"), jobj_users2.getString("agent_id"),
                                    jobj_users2.getString("reg_date"), jobj_users2.getString("user_image"));
                        }
                    }
                    Toast.makeText(applicationContext, " Data Synchronized successfully!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(applicationContext, " No Data to Synchronize from remote server! ", Toast.LENGTH_LONG).show();
                    Toast.makeText(applicationContext, " Data from local server Synchronized successfully!", Toast.LENGTH_LONG).show();
                }
                //Toast.makeText(applicationContext, " Data Synchronized successfully! ", Toast.LENGTH_LONG).show();
            } else {
                //sync agent
                syncAgentNow(a_status, a_designation, a_designation_id);

                Toast.makeText(applicationContext, " Not permitted to synchronize data!", Toast.LENGTH_LONG).show();
            }
        }else {
            //sync agent
            syncAgentNow(a_status, a_designation, a_designation_id);
            if (jobj_users.length()>0){
            //loop through users detail
            for (int i = 0; i < jobj_users.length(); i++) {
                JSONObject jobj_users2 = jobj_users.getJSONObject(i);
                //String test = jobj_users2.getString("fullname");
                //check if user exist and sync
                if (checkForUser(jobj_users2.getString("user_id"))==false) {
                    //sync user detail and save to local
                    syncUserNow(jobj_users2.getString("user_id"), jobj_users2.getString("designation_id"),
                            jobj_users2.getString("designation_type"), jobj_users2.getString("class"),
                            jobj_users2.getString("fullname"), jobj_users2.getString("gender"),
                            jobj_users2.getString("disability"), jobj_users2.getString("parent_guide_name"),
                            jobj_users2.getString("parent_guide_phone"), jobj_users2.getString("dob"),
                            jobj_users2.getString("phone"), jobj_users2.getString("shop_number"),
                            jobj_users2.getString("address"), jobj_users2.getString("bvn"),
                            jobj_users2.getString("acct_name"), jobj_users2.getString("acct_number"),
                            jobj_users2.getString("business_type"), jobj_users2.getString("nok_name"),
                            jobj_users2.getString("nok_number"), jobj_users2.getString("agent_id"),
                            jobj_users2.getString("reg_date"), jobj_users2.getString("user_image"));
                }
            }
                Toast.makeText(applicationContext, " No data to synchronize from local!", Toast.LENGTH_LONG).show();
                Toast.makeText(applicationContext, " Data from remote Synchronized successfully!", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(applicationContext, " No Data to Synchronize! ", Toast.LENGTH_LONG).show();
            }
        }
    }

    //sync agent details to local storage
    private void syncAgentNow(String a_status, String a_designation, String a_designation_id){
        //get required fields
        ContentValues cv=new ContentValues();
        cv.put("status", a_status);
        cv.put("designation", a_designation);
        cv.put("designation_id", a_designation_id);
        String[] whereargs={agent_uid};

        //Update database
        db.update(SQLDBHelper.USER_LOGIN, cv, "username=?", whereargs);
    }

    //check if user already exist in local storage
    private boolean checkForUser(String u_id){
        Boolean return_val;
        String[] args={u_id};
        Cursor cursor=db.rawQuery(String.format("select user_id from %s where user_id=?",SQLDBHelper.USER_TABLE),args);
        if(cursor.getCount()>0){
            return_val=true;
        }else{
            return_val=false;
        }
        return return_val;
    }

    //Sync user details to local storage
    private void syncUserNow(String user_id, String designation_id, String designation_type,
                             String uclass, String fullname, String gender, String disability,
                             String parent_guide_name, String parent_guide_phone, String dob,
                             String phone, String shop_number, String address, String bvn,
                             String acct_name, String acct_number, String business_type,
                             String nok_name, String nok_number, String agent_id, String reg_date, String user_image){
        ContentValues cv=new ContentValues();
        //cv.put("user_id",user_id);
        cv.put("designation_id",designation_id);
        cv.put("designation_type",designation_type);
        cv.put("class",uclass);
        cv.put("fullname",fullname);
        cv.put("gender",gender);
        cv.put("disability",disability);
        cv.put("parent_guide_name",parent_guide_name);
        cv.put("parent_guide_phone",parent_guide_phone);
        cv.put("dob",dob);
        cv.put("phone", phone);
        cv.put("shop_number", shop_number);
        cv.put("address", address);
        cv.put("bvn", bvn);
        cv.put("acct_name", acct_name);
        cv.put("acct_number", acct_number);
        cv.put("business_type", business_type);
        cv.put("nok_name", nok_name);
        cv.put("nok_number", nok_number);
        cv.put("agent_id", agent_id);
        cv.put("reg_date", reg_date);
        cv.put("user_image", user_image);

        //save user details
        int outcome=(int) db.insert(SQLDBHelper.USER_TABLE, null, cv);
    }
}
