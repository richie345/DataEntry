package dataentry.ochanya.com.dataentry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by EMOCHE on 28-Jan-20.
 */

public class AlarmReciever extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String uid=intent.getStringExtra("uid");
        //call the method here
        MobileAsyn ma=new MobileAsyn(context, uid);
        ma.execute();

    }
}
