package dataentry.ochanya.com.dataentry;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimpleReport extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private String uid, role, designation, desig_id;
    private EditText dateTrigger;
    private SQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private RecyclerView.Adapter rAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //First set variables
    private ImageView simple_dateImgFrom, simple_dateImgTo;
    private EditText simple_dateFromPicker, simple_dateToPicker;
    private Button simple_generateBtn;

    //Second set variables
    private RecyclerView simple_recycler;
    private Button simple_closeBtn;
    private LinearLayout simple_btn_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_report);

        //get all intent values
        Intent in=getIntent();
        uid=in.getStringExtra("uid");
        role=in.getStringExtra("role");
        designation=in.getStringExtra("des");
        desig_id=in.getStringExtra("did");

        helper=new SQLDBHelper(SimpleReport.this);
        db=helper.getWritableDatabase();
        layoutManager = new LinearLayoutManager(SimpleReport.this); //Create a Layout Manager

        //First set variables
        simple_dateImgFrom=(ImageView) findViewById(R.id.simple_dateImgFrom);
        simple_dateImgTo=(ImageView) findViewById(R.id.simple_dateImgTo);
        simple_dateFromPicker=(EditText) findViewById(R.id.simple_dateFromPicker);
        simple_dateToPicker=(EditText) findViewById(R.id.simple_dateToPicker);
        simple_generateBtn=(Button) findViewById(R.id.simple_generateBtn);

        //Second set variables
        simple_recycler=(RecyclerView) findViewById(R.id.simple_recycler);
        simple_closeBtn=(Button) findViewById(R.id.simple_closeBtn);
        simple_btn_lay=(LinearLayout) findViewById(R.id.simple_btn_lay);

        //when from date image is clicked
        simple_dateImgFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTrigger=(EditText) simple_dateFromPicker;
                android.support.v4.app.DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(),"date picker");
            }
        });

        //when to date image is clicked
        simple_dateImgTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTrigger=(EditText) simple_dateToPicker;
                android.support.v4.app.DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(),"date picker");
            }
        });

        //When generate button is clicked
        simple_generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String from=simple_dateFromPicker.getText().toString();
                String to=simple_dateToPicker.getText().toString();

                if(!from.equals("") && !to.equals("")){
                    //setRecycler(from, to);

                    String[] args={
                            from,
                            to
                    };
                    Cursor cursor=db.rawQuery(String.format("select * from %s where reg_date>=? and reg_date<=?", SQLDBHelper.USER_TABLE),args);
                    int cursor_count=cursor.getCount();
                    if(cursor_count>0){
                        //Assign cursor value to array to form values for adapter
                        String[] user_fullname=new String[cursor_count];
                        String[] user_reg_date=new String[cursor_count];
                        String[] user_id=new String[cursor_count];
                        int loop_count=0;
                        while(cursor.moveToNext()){
                            user_fullname[loop_count]=cursor.getString(cursor.getColumnIndex("fullname"));
                            user_reg_date[loop_count]=cursor.getString(cursor.getColumnIndex("reg_date"));
                            user_id[loop_count]=cursor.getString(cursor.getColumnIndex("user_id"));
                            loop_count++;
                        }

                        //Work on adapter
                        simple_recycler.setVisibility(View.VISIBLE);
                        simple_recycler.setHasFixedSize(true);
                        simple_recycler.setLayoutManager(layoutManager);
                        rAdapter=new RecyclerAdapter(user_fullname, user_reg_date, user_id);
                        simple_recycler.setAdapter(rAdapter);
                        simple_btn_lay.setVisibility(View.VISIBLE);

                    }else{
                        Toast.makeText(SimpleReport.this, "No user to display!", Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(SimpleReport.this, "Empty field detected!", Toast.LENGTH_LONG).show();
                }
            }
        });

        //when the close button is clicked
        simple_closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder backBuilder=new AlertDialog.Builder(SimpleReport.this);
        backBuilder.setTitle("Warning");
        backBuilder.setMessage("Do you want to close report?");
        backBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        backBuilder.setNegativeButton("No", null);
        backBuilder.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, i);
        c.set(Calendar.MONTH,  i1);
        c.set(Calendar.DAY_OF_MONTH,  i2);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        Date new_date=new Date(currentDateString);
        SimpleDateFormat new_df=new SimpleDateFormat("yyyy-MM-dd");
        String reformated_date=new_df.format(new_date);
        dateTrigger.setText(reformated_date);
    }

    //Function to set Recycler
    /**private void setRecycler(String sdate, String edate){
        String[] args={
                sdate,
                edate
        };
        Cursor cursor=db.rawQuery(String.format("select * from %s where reg_date>=? and reg_date<=?", SQLDBHelper.USER_TABLE),args);
        int cursor_count=cursor.getCount();
        if(cursor_count>0){
            //Assign cursor value to array to form values for adapter
            String[] user_fullname=new String[cursor_count];
            String[] user_reg_date=new String[cursor_count];
            String[] user_id=new String[cursor_count];
            int loop_count=0;
            while(cursor.moveToNext()){
                user_fullname[loop_count]=cursor.getString(cursor.getColumnIndex("fullname"));
                user_reg_date[loop_count]=cursor.getString(cursor.getColumnIndex("reg_date"));
                user_id[loop_count]=cursor.getString(cursor.getColumnIndex("user_id"));
                loop_count++;
            }

            //Work on adapter
            simple_recycler.setHasFixedSize(true);
            simple_recycler.setLayoutManager(layoutManager);
            rAdapter=new RecyclerAdapter(user_fullname, user_reg_date, user_id);
            simple_recycler.setAdapter(rAdapter);
        }else{
            Toast.makeText(SimpleReport.this, "No user to display!", Toast.LENGTH_LONG).show();
        }
    }**/
}
