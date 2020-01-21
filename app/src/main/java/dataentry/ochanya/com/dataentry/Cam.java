package dataentry.ochanya.com.dataentry;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class Cam extends AppCompatActivity {

    SQLiteOpenHelper openhelper;
    SQLiteDatabase db;

    Uri photoPath;
    ImageView ivThumbnailPhoto;
    TextView tv;
    static int TAKE_PICTURE = 1;
    String userid, PAGENAME;
    private String uid, role, designation, desig_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        openhelper=new SQLDBHelper(Cam.this);
        db=openhelper.getWritableDatabase();

        final Button b = (Button) findViewById(R.id.button1);
        final Button b2 = (Button) findViewById(R.id.button2);
        ivThumbnailPhoto = (ImageView) findViewById(R.id.imageView1);
        tv = (TextView)findViewById(R.id.success);
        userid = getIntent().getExtras().getString("user_id");

        Intent in=getIntent();
        uid=in.getStringExtra("uid");
        role=in.getStringExtra("role");
        designation=in.getStringExtra("des");
        desig_id=in.getStringExtra("did");
        PAGENAME = in.getStringExtra("PAGENAME");

        b.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, TAKE_PICTURE);
            }

        });

        b2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder success_alerter=new AlertDialog.Builder(Cam.this);
                success_alerter.setTitle("SUCCESS");
                success_alerter.setMessage("Data saved successfully!");
                success_alerter.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Intent in2=new Intent(Cam.this, AgentDashboard.class);
                        in2.putExtra("uid", uid);
                        in2.putExtra("role", role);
                        in2.putExtra("des", designation);
                        in2.putExtra("did", desig_id);
                        startActivity(in2);
                        finish();
                    }
                });
                success_alerter.show();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        byte[] picData = null;

        if (requestCode == TAKE_PICTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap)intent.getExtras().get("data");
            ivThumbnailPhoto.setImageBitmap(photo);
            ivThumbnailPhoto.setVisibility(View.VISIBLE);

            Bitmap bm = ((BitmapDrawable) ivThumbnailPhoto.getDrawable()).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            picData = stream.toByteArray();
            insertImage(picData);
            tv.setText("Registration Successful");

        }
    }

    public void insertImage(byte[] imageBytes) {
        ContentValues cv = new ContentValues();
        cv.put("user_image", imageBytes);
        db.update("users",cv,"user_id = ?", new String[] { userid });
    }

}