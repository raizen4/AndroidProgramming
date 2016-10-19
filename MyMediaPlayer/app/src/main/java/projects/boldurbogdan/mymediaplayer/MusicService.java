package projects.boldurbogdan.mymediaplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by boldurbogdan on 02/08/2016.
 */

public class MusicService extends Service implements ComponentCallbacks2 {
    MediaPlayer mp = null;
    private static ArrayList<Song> songsToPlay = new ArrayList<>();
    private LocalBinder mBinder = new LocalBinder();
    private static HashMap<String, Boolean> hashMap = new HashMap<>();
    private ArrayList<String> infoToSend = new ArrayList<>();
    private static String nameOfPlaylistCurrentlyPlayed = "null";
    private static String currentSongPlaying = "No song is playing";
    private static int lenght = 0;
    static int songPositionToBePlayed = -1;
    private static boolean isServiceRunning = false;
    static MediaPlayer mpReference = null;
    static boolean shuffleOn = false;
    static boolean repeatOn = false;
    static int totalDurationOfTheSong = 0;
    private static int currentProgressOfTheSong = 0;
    static boolean shouldTriggerNotification = true;
    private static final String PLAY = "projects.boldurbogdan.ACTION_PLAY_PLAY";
    private static final String FFORWARD = "projects.boldurbogdan.ACTION_PLAY_FORWARD";
    private static final String FBACKWARDS = "projects.boldurbogdan.ACTION_PLAY_BACKWARDS";
    private static final String EXIT = "projects.boldurbogdan.ACTION_PLAY_EXIT";
    private static final String UPDATE_MEDIA_CONTROLLER="projects.boldurbogdan.ACTION_UPDATE_MEDIA_CONTROLLER";
    private static final String CloseAPP="projects.boldurbogdan.CLOSEACTIVITIES";
    private static RemoteViews remoteViews;

    public String getCurrentPlayingPlaylist() {
        return nameOfPlaylistCurrentlyPlayed;
    }

    public boolean isServiceRunning() {
      return isServiceRunning;
    }

    public String getCurrentlyPlayingSong() {
        return currentSongPlaying;
    }

    public void setCurrentlyPlaylistPlaying(String newPlaylist) {
        nameOfPlaylistCurrentlyPlayed = newPlaylist;

    }

    public void setShuffleOn(boolean newState) {
        this.shuffleOn = newState;
    }

    public void setRepeatOn(boolean newState) {
        this.repeatOn = newState;
    }

    public void setServiceRunning(boolean serviceRunning) {
        isServiceRunning = serviceRunning;
        if(serviceRunning==false){
        mpReference = mp;
        }
        else{ IntentFilter intentFilter1=new IntentFilter();
            intentFilter1.addAction("PLAY");
            intentFilter1.addAction("FFORWARD");
            intentFilter1.addAction("FBACKWARDS");
            intentFilter1.addAction("EXIT");
            this.registerReceiver(receiverForNotificationReceiver, intentFilter1);

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
    }

    public ArrayList<Song> returnSongArray() {
        return this.songsToPlay;
    }

    public String returnNameOfPlaylistCurrentlyPlayed() {
        return nameOfPlaylistCurrentlyPlayed;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (mpReference == null && mp == null) {
            hashMap.put("PlaylistSongs", false);
            hashMap.put("MainPlayingActivity", false);
            hashMap.put("CurrentlyPlayingActivity", false);

            remoteViews=new RemoteViews(getPackageName(),R.layout.notification_for_media_player);

            mp = new MediaPlayer();

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (shuffleOn == true) {
                        playRandomSong();
                        Intent i = new Intent();
                        i.setAction("projects.boldurbogdan.actionUpdateStatus");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                        updateNotification();
                    } else if (repeatOn == true) {
                        playSameSong();
                        Intent i = new Intent();
                        i.setAction("projects.boldurbogdan.actionUpdateStatus");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                        updateNotification();
                    } else if (shuffleOn == false && repeatOn == false) {
                        playNextSong(-1);
                        Intent i = new Intent();
                        i.setAction("projects.boldurbogdan.actionUpdateStatus");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                        updateNotification();

                    }
                    IntentFilter intentFilter1=new IntentFilter();
                    intentFilter1.addAction("PLAY");
                    intentFilter1.addAction("FFORWARD");
                    intentFilter1.addAction("FBACKWARDS");
                    intentFilter1.addAction("EXIT");
                    registerReceiver(receiverForNotificationReceiver, intentFilter1);
                }
            });
        } else {

            mp = mpReference;


        }

    }


    public void InitiateSongArray(ArrayList<Song> songArrayList, String nameOfPlaylist) {
        this.songsToPlay = songArrayList;
        nameOfPlaylistCurrentlyPlayed = nameOfPlaylist;
    }

    public void playSong(int pos) {
        if(mp==null){
            mp=mpReference;
        }
        songPositionToBePlayed = pos;
        currentSongPlaying = songsToPlay.get(songPositionToBePlayed).getName();
        mp.reset();
        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(getApplicationContext(), Uri.parse(songsToPlay.get(songPositionToBePlayed).getPath()));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        totalDurationOfTheSong = mp.getDuration();
        mp.start();
        Intent i = new Intent(MusicService.this, CurrentlyPlayingActivity.class);
        i.setAction("projects.boldurbogdan.actionUpdateStatus");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
        if (shouldTriggerNotification == true) {
            makeNotification();
            shouldTriggerNotification = false;

        }
        else{
            updateNotification();
        }

    }

    public void resumePlaying(int pos) {
        songPositionToBePlayed = pos;
        currentSongPlaying = songsToPlay.get(songPositionToBePlayed).getName();
        mp.reset();
        try {
            mp.setDataSource(this, Uri.parse(songsToPlay.get(songPositionToBePlayed).getPath()));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.seekTo(lenght);
        mp.start();
        updateNotification();
    }


    public void pauseSong() {
        lenght = mp.getCurrentPosition();
        mp.pause();
        updateNotification();
    }

    public void playNextSong(int position) {
        if(mp==null){
            mp=mpReference;
        }
        if (position == -1) {
            if (songPositionToBePlayed + 1 > songsToPlay.size() - 1) {
                songPositionToBePlayed = 0;
            } else {
                songPositionToBePlayed += 1;
            }
        }
        else if(position+1>songsToPlay.size()-1){
            songPositionToBePlayed =0;
        }
        else{
            songPositionToBePlayed++;
        }
        currentProgressOfTheSong = 0;
        currentSongPlaying = songsToPlay.get(songPositionToBePlayed).getName();
        mp.reset();
        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(this, Uri.parse(songsToPlay.get(songPositionToBePlayed).getPath()));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
        totalDurationOfTheSong = mp.getDuration();
        updateNotification();

    }

    public void playPreviousSong(int position) {
        if(mp==null){
            mp=mpReference;
        }
        if (position == -1) {
            if (songPositionToBePlayed - 1 < 0) {
                songPositionToBePlayed = songsToPlay.size() - 1;
            } else {
                songPositionToBePlayed -= 1;
            }
        }
        else if(position-1<0){
            songPositionToBePlayed = songsToPlay.size()-1;
        }
        else{
            songPositionToBePlayed--;
        }

        currentProgressOfTheSong = 0;
        mp.reset();
        try {
            currentSongPlaying = songsToPlay.get(songPositionToBePlayed).getName();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(this, Uri.parse(songsToPlay.get(songPositionToBePlayed).getPath()));
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
        totalDurationOfTheSong = mp.getDuration();
        updateNotification();
    }

    public boolean getActivityState(String activityName) {
        Log.i("iWasCalled", String.valueOf(hashMap.get(activityName)));
        return hashMap.get(activityName);
    }

    public void setActivityState(String activityname, boolean newState) {
        hashMap.put(activityname, newState);
    }

    public ArrayList<String> giveInfoToCurrentActivity() {
        if (infoToSend.size() > 0) {
            infoToSend.clear();
        }
        infoToSend.add(String.valueOf(lenght));
        infoToSend.add(currentSongPlaying);

        return infoToSend;
    }

    public Song retrieveCurrentSong() {
        if (currentSongPlaying.matches("No song is playing")) {
            return null;
        } else {
            return songsToPlay.get(songPositionToBePlayed);
        }
    }

    private void playRandomSong() {
        Random random = new Random();
        int randomPos = random.nextInt(songsToPlay.size() - 1);
        songPositionToBePlayed = randomPos;
        playSong(songPositionToBePlayed);
        updateNotification();

    }

    public void playSameSong() {
        playSong(songPositionToBePlayed);
        updateNotification();
    }

    public boolean getShuffleState() {
        return shuffleOn;
    }

    public boolean getRepeatState() {
        return repeatOn;
    }

    public int getCurrentProgressOfTheSong() {
        if(mp==null && mpReference!=null){
            return mpReference.getCurrentPosition();
        }
        else{
        return mp.getCurrentPosition();}

    }
    BroadcastReceiver receiverForNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("FBACKWARDS")) {
                Toast.makeText(context,"I received BACKWARDS",Toast.LENGTH_SHORT).show();
                playPreviousSong(songPositionToBePlayed);
                Intent i = new Intent(MusicService.this,MainActivity.class);
                i.setAction("projects.boldurbogdan.actionUpdateStatus");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                Intent i2=new Intent(MusicService.this,PlaylistSongs.class);
                i2.setAction("projects.boldurbogdan.actionUpdateStatus");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i2);

                Intent intentForMediaController=new Intent();
                intentForMediaController.setAction(UPDATE_MEDIA_CONTROLLER);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentForMediaController);


            } else if (intent.getAction().matches("PLAY")) {
                if(mp.isPlaying()){
                    pauseSong();
                    remoteViews.setImageViewResource(R.id.notificationPlayPause,android.R.drawable.ic_media_pause);
                    Toast.makeText(context,"I received PLAY and set it on PAUSE",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MusicService.this,MainActivity.class);
                    i.setAction("projects.boldurbogdan.actionUpdateStatus");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                    Intent i2=new Intent(MusicService.this,PlaylistSongs.class);
                    i2.setAction("projects.boldurbogdan.actionUpdateStatus");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i2);

                    Intent intentForMediaController=new Intent();
                    intentForMediaController.setAction(UPDATE_MEDIA_CONTROLLER);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentForMediaController);
                }
                else{
                    Toast.makeText(context,"I received PLAY",Toast.LENGTH_SHORT).show();
                    resumePlaying(songPositionToBePlayed);
                    Intent i = new Intent(MusicService.this,MainActivity.class);
                    i.setAction("projects.boldurbogdan.actionUpdateStatus");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                    Intent i2=new Intent(MusicService.this,PlaylistSongs.class);
                    i2.setAction("projects.boldurbogdan.actionUpdateStatus");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i2);

                    Intent intentForMediaController=new Intent();
                    intentForMediaController.setAction(UPDATE_MEDIA_CONTROLLER);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentForMediaController);}

            } else if (intent.getAction().matches("FFORWARD")) {
                Toast.makeText(context,"I received FFORWARD",Toast.LENGTH_SHORT).show();
                playNextSong(songPositionToBePlayed);
                Intent i = new Intent(MusicService.this,MainActivity.class);
                i.setAction("projects.boldurbogdan.actionUpdateStatus");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
                Intent i2=new Intent(MusicService.this,PlaylistSongs.class);
                i2.setAction("projects.boldurbogdan.actionUpdateStatus");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i2);


                Intent intentForMediaController=new Intent();
                intentForMediaController.setAction(UPDATE_MEDIA_CONTROLLER);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentForMediaController);

            } else if (intent.getAction().matches("EXIT")) {
                Toast.makeText(context,"I received EXIT",Toast.LENGTH_SHORT).show();
                destroyNotification();



            }
        }
    };

    public void makeNotification() {
        try {
            int ID = 1;

            Intent exitIntent = new Intent(MusicService.this, NotificationReceiver.class);
            exitIntent.setAction(EXIT);
            PendingIntent exitPendingIntent=PendingIntent.getBroadcast(this,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.exit_button,exitPendingIntent);

            Intent playIntent = new Intent(this, NotificationReceiver.class);
            playIntent.setAction(PLAY);
            PendingIntent playPendingIntent=PendingIntent.getBroadcast(this,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.notificationPlayPause,playPendingIntent);


            Intent fForwardIntent = new Intent(this, NotificationReceiver.class);
            fForwardIntent.setAction(FFORWARD);
            PendingIntent fForwardPendingIntent=PendingIntent.getBroadcast(this,0,fForwardIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.notificationForward,fForwardPendingIntent);


            Intent fBackIntent = new Intent(this, NotificationReceiver.class);
            fBackIntent.setAction(FBACKWARDS);
            PendingIntent fBackwardPendingIntent=PendingIntent.getBroadcast(this,0,fBackIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.notificationBackwards,fBackwardPendingIntent);



            Intent intent = new Intent(MusicService.this, CurrentlyPlayingActivity.class);
            Bundle bundle=new Bundle();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.putExtra("Bundle",bundle);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            remoteViews.setImageViewResource(R.id.notificationForward, android.R.drawable.ic_media_next);
            remoteViews.setImageViewResource(R.id.notificationBackwards, android.R.drawable.ic_media_previous);
            remoteViews.setTextViewText(R.id.notificationArtistOfTheSong, retrieveCurrentSong().getArtist());
            remoteViews.setTextViewText(R.id.notifcationNameOfTheSong, (CharSequence) retrieveCurrentSong().getName());

            if (mp.isPlaying()) {
                remoteViews.setImageViewResource(R.id.notificationPlayPause, android.R.drawable.ic_media_pause);
            } else {
                remoteViews.setImageViewResource(R.id.notificationPlayPause, android.R.drawable.ic_media_play);
            }


            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setAutoCancel(true).
                    setSmallIcon(R.drawable.pause).
                    setContent(remoteViews).
                    setOngoing(true).
                    setContentIntent(pendingIntent);

            Notification notification = builder.build();
            notification.bigContentView = remoteViews;
            startForeground(ID,notification);
            Picasso.with(this).
                    load(retrieveCurrentSong().getArt()).
                    placeholder(R.drawable.music_photo).resize(100, 100).
                    into(remoteViews, R.id.notificationPhotoOfTheSong, ID, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNotification() {
        Intent intent = new Intent(MusicService.this, CurrentlyPlayingActivity.class);
        Bundle bundle=new Bundle();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        intent.putExtra("Bundle",bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent exitIntent = new Intent(MusicService.this, NotificationReceiver.class);
        exitIntent.setAction(EXIT);
        PendingIntent exitPendingIntent = PendingIntent.getBroadcast(this, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.exit_button, exitPendingIntent);

        Intent playIntent = new Intent(this, NotificationReceiver.class);
        playIntent.setAction(PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notificationPlayPause, playPendingIntent);


        Intent fForwardIntent = new Intent(this, NotificationReceiver.class);
        fForwardIntent.setAction(FFORWARD);
        PendingIntent fForwardPendingIntent = PendingIntent.getBroadcast(this, 0, fForwardIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notificationForward, fForwardPendingIntent);


        Intent fBackIntent = new Intent(this, NotificationReceiver.class);
        fBackIntent.setAction(FBACKWARDS);
        PendingIntent fBackwardPendingIntent = PendingIntent.getBroadcast(this, 0, fBackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.notificationBackwards, fBackwardPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setAutoCancel(true).setContent(remoteViews).setSmallIcon(R.drawable.pause).setOngoing(true).setContentIntent(pendingIntent);

        remoteViews.setTextViewText(R.id.notificationArtistOfTheSong, retrieveCurrentSong().getArtist());
        remoteViews.setTextViewText(R.id.notifcationNameOfTheSong, retrieveCurrentSong().getName());
        int notificationId = 1;
        if (mp.isPlaying()) {
            remoteViews.setImageViewResource(R.id.notificationPlayPause, android.R.drawable.ic_media_pause);
        } else {
            remoteViews.setImageViewResource(R.id.notificationPlayPause, android.R.drawable.ic_media_play);

        }
        Notification notification = mBuilder.build();

        Picasso.with(this).
                load(retrieveCurrentSong().getArt()).resize(200,200).
                into(remoteViews, R.id.notificationPhotoOfTheSong, notificationId, notification);
        notification.bigContentView = remoteViews;
        notificationManager.notify(notificationId, notification);


    }

    public void destroyNotification(){

        if(mp.isPlaying()) {
            mp.stop();
            mp.reset();
            mp.release();
            mp = null;
        }
        else{
            mp.reset();
            mp.release();
            mp = null;
        }
        if (mpReference!=null) {
             mpReference.release();
            mpReference = null;
        }
        shouldTriggerNotification=true;
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        stopForeground(true);
        notificationManager.cancel(1);
        unregisterReceiver(receiverForNotificationReceiver);
        Intent intentForStoppingActivities=new Intent();
        intentForStoppingActivities.setAction(CloseAPP);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intentForStoppingActivities);
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}

