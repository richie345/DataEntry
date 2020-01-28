package dataentry.ochanya.com.dataentry;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SchoolRegistration extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    SQLiteOpenHelper openhelper;
    SQLiteDatabase db;

    private String uid, role, designation, desig_id;
    private EditText fullname, uclass, phone, email, pg_name, pg_phone;
    private Spinner gender, disability, urole;
    private EditText dob;
    private TextView hide_tv;
    private Button reg;
    private ImageView setdateSR;
    private String PAGENAME = "SCHOOL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_registration);

        //get all intent values
        Intent in=getIntent();
        uid=in.getStringExtra("uid");
        role=in.getStringExtra("role");
        designation=in.getStringExtra("des");
        desig_id=in.getStringExtra("did");

        fullname=(EditText) findViewById(R.id.editText22);
        uclass=(EditText) findViewById(R.id.editText25);
        hide_tv=(TextView) findViewById(R.id.textView3);
        phone=(EditText) findViewById(R.id.editText7);
        email=(EditText) findViewById(R.id.editText8);
        pg_name=(EditText) findViewById(R.id.editText26);
        pg_phone=(EditText) findViewById(R.id.editText27);
        gender=(Spinner) findViewById(R.id.spinner5);
        urole=(Spinner)  findViewById(R.id.spinner5b);
        disability=(Spinner) findViewById(R.id.spinner2);
        dob=(EditText) findViewById(R.id.datePicker);
        reg=(Button) findViewById(R.id.button2);
        setdateSR = (ImageView)findViewById(R.id.schoolR);


        setdateSR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(),"date picker");


            }
        });


        openhelper=new SQLDBHelper(SchoolRegistration.this);
        db=openhelper.getWritableDatabase();

        //click of role
        urole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    uclass.setText("nul");
                    uclass.setVisibility(View.GONE);
                    hide_tv.setVisibility(View.GONE);
                }else{
                    uclass.setText("");
                    uclass.setVisibility(View.VISIBLE);
                    hide_tv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //register school
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname_val=fullname.getText().toString();
                String uclass_val=uclass.getText().toString();
                String phone_val=phone.getText().toString();
                String email_val=email.getText().toString();
                String pg_name_val=pg_name.getText().toString();
                String pg_phone_val=pg_phone.getText().toString();
                String gender_val=gender.getSelectedItem().toString();
                String disability_val= disability.getSelectedItem().toString();
                String dob_val=dob.getText().toString();
                String urole_val=urole.getSelectedItem().toString();

                if(!fullname_val.equals("") && !uclass_val.equals("") && !phone_val.equals("") &&
                        !email_val.equals("") && !pg_name_val.equals("") && !pg_phone_val.equals("")
                        && !gender_val.equals("") && !disability_val.equals("") && !dob_val.equals("")
                        && !urole_val.equals("")){

                    //get registration date
                    Date date=new Date();
                    SimpleDateFormat simpledateformat=new SimpleDateFormat("yyyy-MM-dd");
                    String reg_date=simpledateformat.format(date);
                    //Send to database
                    ContentValues groupedValues=new ContentValues();
                    groupedValues.put("user_id", email_val);
                    groupedValues.put("designation_id", desig_id);
                    groupedValues.put("designation_type", "school");
                    groupedValues.put("class", uclass_val);
                    groupedValues.put("fullname", fullname_val);
                    groupedValues.put("gender", gender_val);
                    groupedValues.put("disability", disability_val);
                    groupedValues.put("parent_guide_name", pg_name_val);
                    groupedValues.put("parent_guide_phone", pg_phone_val);
                    groupedValues.put("dob", dob_val);
                    groupedValues.put("phone", phone_val);
                    groupedValues.put("shop_number", "");
                    groupedValues.put("address", "");
                    groupedValues.put("bvn", "");
                    groupedValues.put("acct_name", "");
                    groupedValues.put("acct_number", "");
                    groupedValues.put("business_type", "");
                    groupedValues.put("nok_name", "");
                    groupedValues.put("nok_number", "");
                    groupedValues.put("agent_id", uid);
                    groupedValues.put("reg_date", reg_date);
                    groupedValues.put("role", urole_val);

                    int res=(int) db.insert(SQLDBHelper.USER_TABLE, null, groupedValues);

                    if(res>0){
                        Intent i = new Intent(SchoolRegistration.this, Cam.class);
                        i.putExtra("user_id", email_val);
                        i.putExtra("PAGENAME", PAGENAME);

                        i.putExtra("uid", uid);
                        i.putExtra("role", role);
                        i.putExtra("des", designation);
                        i.putExtra("did", desig_id);
                        startActivity(i);
                        finish();

                       /* AlertDialog.Builder success_alerter=new AlertDialog.Builder(SchoolRegistration.this);
                        success_alerter.setTitle("SUCCESS");
                        success_alerter.setMessage("Data saved successfully!");
                        success_alerter.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Intent in2=new Intent(SchoolRegistration.this, AgentDashboard.class);
                                in2.putExtra("uid", uid);
                                in2.putExtra("role", role);
                                in2.putExtra("des", designation);
                                in2.putExtra("did", desig_id);
                                startActivity(in2);
                                finish();
                            }
                        });
                        success_alerter.show(); */
                    }else{
                        AlertDialog.Builder error_alerter=new AlertDialog.Builder(SchoolRegistration.this);
                        error_alerter.setTitle("ERROR");
                        error_alerter.setMessage("Operation Failed!");
                        error_alerter.show();
                    }
                }else{
                    Toast.makeText(SchoolRegistration.this, "empty field detected", Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder backBuilder=new AlertDialog.Builder(SchoolRegistration.this);
        backBuilder.setTitle("Warning");
        backBuilder.setMessage("Do you want to close this form?");
        backBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent backInt=new Intent(SchoolRegistration.this, AgentDashboard.class);
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
