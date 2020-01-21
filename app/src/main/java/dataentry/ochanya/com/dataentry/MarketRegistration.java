package dataentry.ochanya.com.dataentry;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MarketRegistration extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    SQLiteOpenHelper openhelper;
    SQLiteDatabase db;
    Date dt;

    private EditText fullname, phone, email, business_type, address, bvn,
            acct_name, acct_numb, nok, nok_phone, shop_numb;
    private Spinner gender, disability;
    private EditText dob;
    private Button saveReg;
    private String uid, role, designation, desig_id;
    private ImageView setdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_registration);

        //get all intent values
        Intent in=getIntent();
        uid=in.getStringExtra("uid");
        role=in.getStringExtra("role");
        designation=in.getStringExtra("des");
        desig_id=in.getStringExtra("did");

        fullname=(EditText) findViewById(R.id.editText22);
        gender=(Spinner) findViewById(R.id.spinner3);
        dob= (EditText) findViewById(R.id.datePicker);
        phone=(EditText) findViewById(R.id.editText25);
        email=(EditText) findViewById(R.id.editText17);
        business_type=(EditText) findViewById(R.id.editText26);
        address=(EditText) findViewById(R.id.editText27);
        bvn=(EditText) findViewById(R.id.editText3);
        acct_name=(EditText) findViewById(R.id.editText5);
        acct_numb=(EditText) findViewById(R.id.editText11);
        nok=(EditText) findViewById(R.id.editText13);
        nok_phone=(EditText) findViewById(R.id.editText16);
        disability=(Spinner) findViewById(R.id.spinner4);
        saveReg=(Button) findViewById(R.id.button2);
        shop_numb=(EditText) findViewById(R.id.spinner);
       setdate = (ImageView) findViewById(R.id.MR_datepicker);


       setdate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               android.support.v4.app.DialogFragment datepicker = new DatePickerFragment();
               datepicker.show(getSupportFragmentManager(),"date picker");


           }
       });

        openhelper=new SQLDBHelper(MarketRegistration.this);
        db=openhelper.getWritableDatabase();

        saveReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  fullname_val=fullname.getText().toString();
                String  gender_val=gender.getSelectedItem().toString();
                String  dob_val=dob.getText().toString(); //-----------------------
                String  phone_val=phone.getText().toString();
                String  email_val=email.getText().toString();
                String  business_type_val= business_type.getText().toString();
                String  address_val=address.getText().toString();
                String  bvn_val=bvn.getText().toString();
                String  acct_name_val=acct_name.getText().toString();
                String  acct_numb_val=acct_numb.getText().toString();
                String  nok_val=nok.getText().toString();
                String  nok_phone_val=nok_phone.getText().toString();
                String disability_val=disability.getSelectedItem().toString();
                String shop_numb_val=shop_numb.getText().toString();

                if(!fullname_val.equals("") && !gender_val.equals("") && !dob_val.equals("") &&
                        !phone_val.equals("") && !email_val.equals("") &&
                        !business_type_val.equals("") && !address_val.equals("") &&
                        !bvn_val.equals("") && !acct_name_val.equals("") &&
                        !acct_numb_val.equals("") && !nok_val.equals("") &&
                        !nok_phone_val.equals("") && !disability_val.equals("") && !shop_numb_val.equals("")){
                    //get registration date
                    Date date=new Date();
                    SimpleDateFormat simpledateformat=new SimpleDateFormat("yyyy-MM-dd");
                    String reg_date=simpledateformat.format(date);
                    //Send to database
                    ContentValues groupedValues=new ContentValues();
                    groupedValues.put("user_id", email_val);
                    groupedValues.put("designation_id", desig_id);
                    groupedValues.put("designation_type", "market");
                    groupedValues.put("class", "");
                    groupedValues.put("fullname", fullname_val);
                    groupedValues.put("gender", gender_val);
                    groupedValues.put("disability", disability_val);
                    groupedValues.put("parent_guide_name", "");
                    groupedValues.put("parent_guide_phone", "");
                    groupedValues.put("dob", dob_val);
                    groupedValues.put("phone", phone_val);
                    groupedValues.put("shop_number", shop_numb_val);
                    groupedValues.put("address", address_val);
                    groupedValues.put("bvn", bvn_val);
                    groupedValues.put("acct_name", acct_name_val);
                    groupedValues.put("acct_number", acct_numb_val);
                    groupedValues.put("business_type", business_type_val);
                    groupedValues.put("nok_name", nok_val);
                    groupedValues.put("nok_number", nok_phone_val);
                    groupedValues.put("agent_id", uid);
                    groupedValues.put("reg_date", reg_date);
                    int res=(int) db.insert(SQLDBHelper.USER_TABLE, null, groupedValues);

                    if(res>0){
                        AlertDialog.Builder success_alerter=new AlertDialog.Builder(MarketRegistration.this);
                        success_alerter.setTitle("SUCCESS");
                        success_alerter.setMessage("Data saved successfully!");
                        success_alerter.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Intent in2=new Intent(MarketRegistration.this, AgentDashboard.class);
                                in2.putExtra("uid", uid);
                                in2.putExtra("role", role);
                                in2.putExtra("des", designation);
                                in2.putExtra("did", desig_id);
                                startActivity(in2);
                                finish();
                            }
                        });
                        success_alerter.show();
                    }else{
                        AlertDialog.Builder error_alerter=new AlertDialog.Builder(MarketRegistration.this);
                        error_alerter.setTitle("ERROR");
                        error_alerter.setMessage("Operation Failed!");
                        error_alerter.show();
                    }
                }else{
                    Toast.makeText(MarketRegistration.this, "empty field detected", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH,  month);
        c.set(Calendar.DAY_OF_MONTH,  day);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        Date new_date=new Date(currentDateString);
        SimpleDateFormat new_df=new SimpleDateFormat("yyyy-MM-dd");
        String reformated_date=new_df.format(new_date);
        dob.setText(reformated_date);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder backBuilder=new AlertDialog.Builder(MarketRegistration.this);
        backBuilder.setTitle("Warning");
        backBuilder.setMessage("Do you want to close this form?");
        backBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent backInt=new Intent(MarketRegistration.this, AgentDashboard.class);
                backInt.putExtra("uid", uid);
                backInt.putExtra("role", role);
                backInt.putExtra("des", designation);
                backInt.putExtra("did", desig_id);
                startActivity(backInt);
                finish();
            }
        });
        backBuilder.setNegativeButton("No", null);
        backBuilder.show();
    }
}
