package dataentry.ochanya.com.dataentry;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.util.TimerTask;

/**
 * Created by Ephraim on 1/25/2020.
 */

public class DoTimerTask extends TimerTask {
    private Context cnt;
    private Handler mHandler;

    DoTimerTask(Context contect){
        cnt=contect;
    }

    @Override
    public void run() {
        Looper.prepare();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // process incoming messages here
                Toast.makeText(cnt, "this is working", Toast.LENGTH_SHORT).show();
            }
        };
        Looper.loop();
    }
}
