package dataentry.ochanya.com.dataentry;

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
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SendMessage extends AppCompatActivity {
    private Button submit;
    private EditText agent, title, message;
    private String uid, role, designation, desig_id;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        openHelper=new SQLDBHelper(SendMessage.this);
        db=openHelper.getWritableDatabase();

        //get all intent values
        Intent in=getIntent();
        uid=in.getStringExtra("uid");
        role=in.getStringExtra("role");
        designation=in.getStringExtra("des");
        desig_id=in.getStringExtra("did");

        agent = (EditText) findViewById(R.id.receiver);
        title = (EditText) findViewById(R.id.mtitle);
        message = (EditText) findViewById(R.id.msg);

        submit = (Button) findViewById(R.id.button2);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiver=agent.getText().toString();
                String mtitle=title.getText().toString();
                String msg=message.getText().toString();

                if(!receiver.equals("") && !mtitle.equals("") && !msg.equals("")){

                    //get registration date
                    Date date=new Date();
                    SimpleDateFormat simpledateformat=new SimpleDateFormat("yyyy-MM-dd");
                    String reg_date=simpledateformat.format(date);
                    //Send to database
                    ContentValues groupedValues=new ContentValues();
                    groupedValues.put("title", mtitle);
                    groupedValues.put("message", msg);
                    groupedValues.put("sender", uid);
                    groupedValues.put("receiver", receiver);
                    groupedValues.put("readstatus", "unread");
                    groupedValues.put("solvedstatus", "unsolved");
                    groupedValues.put("msgstatus", "active");

                    int res=(int) db.insert(SQLDBHelper.MESSAGES, null, groupedValues);

                    if(res>0){
                        AlertDialog.Builder success_alerter=new AlertDialog.Builder(SendMessage.this);
                        success_alerter.setTitle("SUCCESS");
                        success_alerter.setMessage("Message sent successfully!");
                        success_alerter.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                Intent in2=new Intent(SendMessage.this, AgentDashboard.class);
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
                        AlertDialog.Builder error_alerter=new AlertDialog.Builder(SendMessage.this);
                        error_alerter.setTitle("ERROR");
                        error_alerter.setMessage("Operation Failed!");
                        error_alerter.show();
                    }
                }else{
                    Toast.makeText(SendMessage.this, "empty field detected", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
