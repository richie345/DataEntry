package dataentry.ochanya.com.dataentry;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AgentReport extends AppCompatActivity {
    private Button rpt_by_des, rpt_simple;
    private String uid, role, designation, desig_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_report);

        //get all intent values
        Intent in=getIntent();
        uid=in.getStringExtra("uid");
        role=in.getStringExtra("role");
        designation=in.getStringExtra("des");
        desig_id=in.getStringExtra("did");

        rpt_by_des=(Button) findViewById(R.id.rpt_by_des);
        rpt_simple=(Button) findViewById(R.id.rpt_simple);

        //set click listener for report by designation
        rpt_by_des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //set click listener for simple report
        rpt_simple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(AgentReport.this,SimpleReport.class);
                in.putExtra("uid", uid);
                in.putExtra("role", role);
                in.putExtra("des", designation);
                in.putExtra("did", desig_id);
                startActivity(in);
            }
        });
    }

    @Override
    public void onBackPressed() {
        onBackPressed();
    }
}
