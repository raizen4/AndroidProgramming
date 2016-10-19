package projects.boldurbogdan.mymediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * Created by boldurbogdan on 05/09/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    private static final String PLAY="projects.boldurbogdan.ACTION_PLAY_PLAY";
    private static final String FFORWARD="projects.boldurbogdan.ACTION_PLAY_FORWARD";
    private static final String FBACKWARDS="projects.boldurbogdan.ACTION_PLAY_BACKWARDS";
    private static final String EXIT="projects.boldurbogdan.ACTION_PLAY_EXIT";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches(PLAY)) {
            Intent play=new Intent();
            play.setAction("PLAY");
            context.sendBroadcast(play);

        } else if (intent.getAction().matches(FFORWARD)) {
            Intent FFORWARD=new Intent();
            FFORWARD.setAction("FFORWARD");
            context.sendBroadcast(FFORWARD);

        } else if (intent.getAction().matches(FBACKWARDS)) {
            Intent FBACKWARDS=new Intent();
            FBACKWARDS.setAction("FBACKWARDS");
            context.sendBroadcast(FBACKWARDS);
        }
        else if(intent.getAction().matches(EXIT)){
            Intent EXIT=new Intent();
            EXIT.setAction("EXIT");
            context.sendBroadcast(EXIT);

        }
    }}

