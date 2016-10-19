package projects.boldurbogdan.mymediaplayer;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CurrentlyPlayingActivity extends AppCompatActivity implements SeekBar.OnDragListener {
    private ImageButton playIt;
    private ImageButton fForward;
    private ImageButton fBackwards;
    private ImageButton shuffle;
    private ImageButton repeat;
    private ImageView imageForTheSong;
    private TextView nameOfTheSong;
    private TextView totalDurationOfTheSong;
    private TextView currentTimeElapsed;
    protected projects.boldurbogdan.mymediaplayer.MusicService musicService = null;
    protected projects.boldurbogdan.mymediaplayer.MusicService musicServiceForBackground = null;
    private boolean isBound = false;
    private Uri imageOfTheSong;
    private Song currentSongPlaying;
    private int currentPosPlayingInPlaylist = 0;
    private ProgressBar bar;
    private String durationOfTheSong;
    private android.os.Handler mHandler = new android.os.Handler();
    private String whichPlaylistIsPlayingNow = "null";
    private   boolean isActivityVisible=false;
    private Toolbar toolbar;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("projects.boldurbogdan.actionUpdateStatus")) {
                if (isActivityVisible) {
                    ArrayList<String> infoToUpdate = musicService.giveInfoToCurrentActivity();
                    nameOfTheSong.setText(infoToUpdate.get(1), TextView.BufferType.EDITABLE);
                    currentSongPlaying = musicService.retrieveCurrentSong();
                    whichPlaylistIsPlayingNow = musicService.getCurrentPlayingPlaylist();
                    mHandler.postDelayed(updateCurrentTimeElapsed, 100);
                    mHandler.postDelayed(updateSeekBar, 1000);
                    Toast.makeText(getApplicationContext(), currentSongPlaying.getName(), Toast.LENGTH_SHORT).show();
                    Picasso.with(getApplicationContext()).
                            load(currentSongPlaying.getArt()).placeholder(R.drawable.music_photo).resize(300, 300).into(imageForTheSong);
                    totalDurationOfTheSong.setText(currentSongPlaying.getDuration());
                    bar.setMax(musicService.mp.getDuration());
                    bar.setProgress(musicService.getCurrentProgressOfTheSong());
                    if (musicService.mp.isPlaying()) {
                        playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));

                    } else {
                        playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                    }
                }
            }
            }


    };
    private BroadcastReceiver receiverForStoppingActivity=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().matches("projects.boldurbogdan.CLOSEACTIVITIES")){
                finishAffinity();
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
            bindService();

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle receivedBundle=getIntent().getExtras();


        try { String shouldUpdateActivity=receivedBundle.getString("UPDATE");
            Toast.makeText(getApplicationContext(),"SHOULD UPDATE? "+shouldUpdateActivity,Toast.LENGTH_SHORT).show();
            if(shouldUpdateActivity!=null && shouldUpdateActivity.matches("YES")){
                ArrayList<String> infoToUpdate = musicService.giveInfoToCurrentActivity();
                nameOfTheSong.setText(infoToUpdate.get(1), TextView.BufferType.EDITABLE);
                currentSongPlaying=musicService.retrieveCurrentSong();
                whichPlaylistIsPlayingNow=musicService.getCurrentPlayingPlaylist();
                mHandler.postDelayed(updateCurrentTimeElapsed,100);
                mHandler.postDelayed(updateSeekBar,1000);
                Toast.makeText(getApplicationContext(),currentSongPlaying.getName(),Toast.LENGTH_SHORT).show();
                Picasso.with(getApplicationContext()).
                        load(currentSongPlaying.getArt()).placeholder(R.drawable.music_photo).resize(300,300).into(imageForTheSong);
                durationOfTheSong=currentSongPlaying.getDuration();
                totalDurationOfTheSong.setText(currentSongPlaying.getDuration());
                //totalDurationOfTheSong.setText(String.valueOf(String.valueOf(durationOfTheSong/100)+":"+String.valueOf(durationOfTheSong%100)),TextView.BufferType.EDITABLE);
                bar.setMax(musicService.mp.getDuration());
                bar.setProgress(musicService.getCurrentProgressOfTheSong());
                if(musicService.getShuffleState()==true){
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_pressed));
                }
                else{
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle));
                }
                if(musicService.getRepeatState()==true){
                    repeat.setBackground(getResources().getDrawable(R.drawable.repeat_pressed));
                }
                else{
                    repeat.setBackground(getResources().getDrawable(R.drawable.repeat));
                }

                if (musicService.mp.isPlaying()) {
                    playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));

                } else {
                    playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }


        setContentView(R.layout.activity_currently_playing);
        playIt= (ImageButton) findViewById(R.id.playIt);
        fForward= (ImageButton) findViewById(R.id.fastForward);
        fBackwards= (ImageButton) findViewById(R.id.fastBackward);
        shuffle= (ImageButton) findViewById(R.id.shuffle);
        repeat= (ImageButton) findViewById(R.id.repeat);
        imageForTheSong=(ImageView) findViewById(R.id.ImageForTheSong);
        nameOfTheSong= (TextView) findViewById(R.id.nameOfTheSong);
        bar= (SeekBar) findViewById(R.id.barForTheSongCompletion);
        totalDurationOfTheSong= (TextView) findViewById(R.id.totalDuration);
        currentTimeElapsed= (TextView) findViewById(R.id.beginingOfTheSong);
        toolbar= (Toolbar) findViewById(R.id.toolbarForCurrentlyPlayingActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Now playing...");

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver,new IntentFilter("projects.boldurbogdan.actionUpdateStatus"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiverForStoppingActivity,new IntentFilter("projects.boldurbogdan.CLOSEACTIVITIES"));
        SetupMediaControllerButtons();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_for_currently_playing_activity,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.go_to_songs:
                stopUpdatingSeekBar();
                stopUpdatingTimeElapsed();
                musicService.setActivityState("CurrentlyPlayingActivity",false);
                unbindService();
                isBound=false;
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
                LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiverForStoppingActivity);
                musicService=null;
                finish();
                if(whichPlaylistIsPlayingNow.matches("ALL")){
                    Intent i=new Intent(this,MainActivity.class);
                    startActivity(i);

                }
                else{
                    Intent i=new Intent(this,PlaylistSongs.class);
                    i.putExtra("code",1);
                    i.putExtra("nameOfPlaylist",whichPlaylistIsPlayingNow);
                    startActivity(i);
                }
                break;
            case R.id.show_lirics:
            case R.id.add_songs_to_playlist:
                final String[] nameOfPlaylistChosen = new String[1];
                View customViewForAlertDialog=getLayoutInflater().inflate(R.layout.alert_dialog_for_adding_songs_to_playlists,null);
                final ListView playlistListview= (ListView) customViewForAlertDialog.findViewById(R.id.playlistListviewForAlertDialog);
                final DatabaseHelper helper=new DatabaseHelper(getApplicationContext());
                final ArrayList<Playlist> playlists=helper.getPlaylists();
                PlaylistAdapterForAlertDialog adapterForAlertDialog=new PlaylistAdapterForAlertDialog(getApplicationContext(),playlists);
                playlistListview.setAdapter(adapterForAlertDialog);
                playlistListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        nameOfPlaylistChosen[0] =playlists.get(position).getName();
                        playlistListview.setItemChecked(position,true);

                    }
                });
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("ADD",null).setView(customViewForAlertDialog);
                final AlertDialog dialogForAddingSongs=builder.create();
                dialogForAddingSongs.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button buttonAdd= (Button) dialogForAddingSongs.getButton(AlertDialog.BUTTON_POSITIVE);
                        buttonAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (nameOfPlaylistChosen[0].isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "no playlist chosen", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), "playlist" + nameOfPlaylistChosen[0] + " has been chosen", Toast.LENGTH_SHORT).show();

                                    if (helper.songAlreadyInPlaylist(musicService.retrieveCurrentSong().getId(), nameOfPlaylistChosen[0]) == 0) {
                                        helper.updateNumberOfTracks(nameOfPlaylistChosen[0], helper.getNumberOfTracksForPlaylist(nameOfPlaylistChosen[0]) + 1);
                                        helper.insertSongInPlyalist(musicService.retrieveCurrentSong().getId(), nameOfPlaylistChosen[0]);
                                        Toast.makeText(getApplicationContext(), "Song added in playlist", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Song already in playlist", Toast.LENGTH_SHORT).show();
                                    }

                                    dialogForAddingSongs.dismiss();
                                }
                            }
                        });
                    }
                });
                dialogForAddingSongs.show();
                break;

                default:
                    return super.onOptionsItemSelected(item);
        }


    return false;
    }

    ServiceConnection musicConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            projects.boldurbogdan.mymediaplayer.MusicService.LocalBinder binder= (projects.boldurbogdan.mymediaplayer.MusicService.LocalBinder) service;
            musicService=binder.getService();
            musicServiceForBackground=musicService;
            isBound=true;
            checkActivity();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound=false;
            musicService=null;

        }
        private void checkActivity(){
            try {
                if(musicService.isServiceRunning()==false){
                    musicService.setServiceRunning(true);
                    Toast.makeText(getApplicationContext(),"the service is running",Toast.LENGTH_SHORT).show();
                }

                if (musicService.getActivityState("CurrentlyPlayingActivity")==false) {
                    musicService.setActivityState("CurrentlyPlayingActivity",true);
                    isActivityVisible=true;
                    musicService.updateNotification();
                    Toast.makeText(getApplicationContext(),"Activity main was not visible until now",Toast.LENGTH_SHORT).show();
                    ArrayList<String> infoToUpdate = musicService.giveInfoToCurrentActivity();
                    nameOfTheSong.setText(infoToUpdate.get(1), TextView.BufferType.EDITABLE);
                    currentSongPlaying=musicService.retrieveCurrentSong();
                    whichPlaylistIsPlayingNow=musicService.getCurrentPlayingPlaylist();
                    mHandler.postDelayed(updateCurrentTimeElapsed,100);
                    mHandler.postDelayed(updateSeekBar,1000);
                    Toast.makeText(getApplicationContext(),currentSongPlaying.getName(),Toast.LENGTH_SHORT).show();
                        Picasso.with(getApplicationContext()).
                                load(currentSongPlaying.getArt()).placeholder(R.drawable.music_photo).resize(300,300).into(imageForTheSong);
                        totalDurationOfTheSong.setText(currentSongPlaying.getDuration());
                        //durationOfTheSong=currentSongPlaying.real_duration_in_minutes;
                        //totalDurationOfTheSong.setText(String.valueOf(String.valueOf(durationOfTheSong/100)+":"+String.valueOf(durationOfTheSong%100)),TextView.BufferType.EDITABLE);
                        bar.setMax(musicService.mp.getDuration());
                        bar.setProgress(musicService.getCurrentProgressOfTheSong());
                        if(musicService.getShuffleState()==true){
                            shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_pressed));
                        }
                    else{
                            shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle));
                        }
                        if(musicService.getRepeatState()==true){
                            repeat.setBackground(getResources().getDrawable(R.drawable.repeat_pressed));
                        }
                        else{
                            repeat.setBackground(getResources().getDrawable(R.drawable.repeat));
                        }

                    if (musicService.mp.isPlaying()) {
                        playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));

                    } else {
                        playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
                    }


                }
            }catch (Exception e){
                e.toString();
            }

        }
    };

    private void SetupMediaControllerButtons() {
        playIt = (ImageButton) findViewById(R.id.playIt);
        fForward = (ImageButton) findViewById(R.id.fastForward);
        fBackwards = (ImageButton)findViewById(R.id.fastBackward);
        nameOfTheSong= (TextView) findViewById(R.id.nameOfTheSong);
        playIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (musicService.mp.isPlaying()) {
                        musicService.pauseSong();
                        playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));

                    } else {
                        Log.i("current pos", String.valueOf(musicService.songPositionToBePlayed));
                        musicService.resumePlaying(musicService.songPositionToBePlayed);
                        playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));


                    }


            }
        });
        fForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService.mp != null) {
                    musicService.playNextSong(musicService.songPositionToBePlayed);
                    playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    currentSongPlaying=musicService.retrieveCurrentSong();
                    nameOfTheSong.setText(musicService.getCurrentlyPlayingSong(),TextView.BufferType.EDITABLE);
                    Picasso.with(getApplicationContext()).
                            load(currentSongPlaying.getArt()).placeholder(R.drawable.music_photo).resize(300,300).into(imageForTheSong);
                    totalDurationOfTheSong.setText(String.format("%s:%s",
                            TimeUnit.MILLISECONDS.toMinutes(musicService.totalDurationOfTheSong),
                            TimeUnit.MILLISECONDS.toSeconds((musicService.totalDurationOfTheSong)/10)), TextView.BufferType.EDITABLE);
                    bar.setMax(musicService.mp.getDuration());
                    bar.setProgress(musicService.getCurrentProgressOfTheSong());
                }
            }
        });
        fBackwards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService.mp != null) {
                    musicService.playPreviousSong(musicService.songPositionToBePlayed);
                    playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));
                    currentSongPlaying=musicService.retrieveCurrentSong();
                    nameOfTheSong.setText(musicService.getCurrentlyPlayingSong(), TextView.BufferType.EDITABLE);
                    Picasso.with(getApplicationContext()).
                            load(currentSongPlaying.getArt()).placeholder(R.drawable.music_photo).resize(300,300).into(imageForTheSong);
                    totalDurationOfTheSong.setText(String.format("%s:%s",
                            TimeUnit.MILLISECONDS.toMinutes(musicService.totalDurationOfTheSong),
                            TimeUnit.MILLISECONDS.toSeconds((musicService.totalDurationOfTheSong)/10)), TextView.BufferType.EDITABLE);
                    bar.setMax(musicService.mp.getDuration());
                    bar.setProgress(musicService.getCurrentProgressOfTheSong());
                }
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicService.getRepeatState()==true){
                    musicService.setRepeatOn(false);
                    repeat.setBackground(getResources().getDrawable(R.drawable.repeat));
                }
                if(musicService.getShuffleState()==false){
                    musicService.setShuffleOn(true);
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_pressed));
                    Toast.makeText(getApplicationContext(),"shuffle pressed",Toast.LENGTH_SHORT).show();
                }
                else if(musicService.getShuffleState()==true){
                    musicService.setShuffleOn(false);
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle));
                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(musicService.getShuffleState()==true){
                    musicService.setShuffleOn(false);
                    shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle));
                }
                if(musicService.getRepeatState()==false){
                    musicService.setRepeatOn(true);
                    repeat.setBackground(getResources().getDrawable(R.drawable.repeat_pressed));
                }
                else if(musicService.getRepeatState()==true){
                    musicService.setRepeatOn(false);
                    repeat.setBackground(getResources().getDrawable(R.drawable.repeat));
                }
            }
        });
    }
    private void bindService(){
        Intent serviceIntent=new Intent(this, projects.boldurbogdan.mymediaplayer.MusicService.class);
        bindService(serviceIntent,musicConnection,BIND_AUTO_CREATE);
        isBound=true;


    }
    private void unbindService(){
        isActivityVisible=false;
        musicService.setServiceRunning(false);
        unbindService(musicConnection);

    }

    public Runnable updateSeekBar=new Runnable() {
        @Override
        public void run() {
            bar.setProgress(musicService.getCurrentProgressOfTheSong());
            mHandler.postDelayed(this,1000);

        }
    };
    public Runnable updateCurrentTimeElapsed=new Runnable() {
        @Override
        public void run() {
                if(isBound==false){
                    mHandler.removeCallbacks(updateCurrentTimeElapsed);
                }
            else {
                    currentTimeElapsed.setText(String.format("%s:%s",
                            TimeUnit.MILLISECONDS.toMinutes(musicService.mp.getCurrentPosition()),
                            TimeUnit.MILLISECONDS.toSeconds(musicService.mp.getCurrentPosition())%60));
                    mHandler.postDelayed(this, 100);
                }

        }
    };
    public void stopUpdatingSeekBar(){
       mHandler.removeCallbacks(updateSeekBar);
    }
    public void stopUpdatingTimeElapsed(){
        mHandler.removeCallbacks(updateCurrentTimeElapsed);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopUpdatingSeekBar();
        stopUpdatingTimeElapsed();
        musicService.setActivityState("CurrentlyPlayingActivity",false);
        unbindService();
        isBound=false;
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiverForStoppingActivity);
        musicService=null;
        finish();
        if(whichPlaylistIsPlayingNow.matches("ALL")){
            Intent i=new Intent(this,MainActivity.class);
            startActivity(i);

        }
        else{
            Intent i=new Intent(this,PlaylistSongs.class);
            i.putExtra("code",1);
            i.putExtra("nameOfPlaylist",whichPlaylistIsPlayingNow);
            startActivity(i);
        }


    }

    @Override
    public boolean onDrag(View v, DragEvent event) {

        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus){
            stopUpdatingSeekBar();
            stopUpdatingTimeElapsed();

        }
        else{
            ArrayList<String> infoToUpdate = musicService.giveInfoToCurrentActivity();
            nameOfTheSong.setText(infoToUpdate.get(1), TextView.BufferType.EDITABLE);
            currentSongPlaying=musicService.retrieveCurrentSong();
            whichPlaylistIsPlayingNow=musicService.getCurrentPlayingPlaylist();
            mHandler.postDelayed(updateCurrentTimeElapsed,100);
            mHandler.postDelayed(updateSeekBar,1000);
            Toast.makeText(getApplicationContext(),currentSongPlaying.getName(),Toast.LENGTH_SHORT).show();
            Picasso.with(getApplicationContext()).
                    load(currentSongPlaying.getArt()).placeholder(R.drawable.music_photo).resize(300,300).into(imageForTheSong);
            totalDurationOfTheSong.setText(currentSongPlaying.getDuration());
            //durationOfTheSong=currentSongPlaying.real_duration_in_minutes;
            //totalDurationOfTheSong.setText(String.valueOf(String.valueOf(durationOfTheSong/100)+":"+String.valueOf(durationOfTheSong%100)),TextView.BufferType.EDITABLE);
            bar.setMax(musicService.mp.getDuration());
            bar.setProgress(musicService.getCurrentProgressOfTheSong());
            if(musicService.getShuffleState()==true){
                shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle_pressed));
            }
            else{
                shuffle.setBackground(getResources().getDrawable(R.drawable.shuffle));
            }
            if(musicService.getRepeatState()==true){
                repeat.setBackground(getResources().getDrawable(R.drawable.repeat_pressed));
            }
            else{
                repeat.setBackground(getResources().getDrawable(R.drawable.repeat));
            }

            if (musicService.mp.isPlaying()) {
                playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_pause));

            } else {
                playIt.setBackground(getResources().getDrawable(android.R.drawable.ic_media_play));
            }
            isActivityVisible=true;

        }
    }
}


