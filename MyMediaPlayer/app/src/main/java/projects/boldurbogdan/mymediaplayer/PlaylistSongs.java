package projects.boldurbogdan.mymediaplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class PlaylistSongs extends AppCompatActivity{
    private Toolbar toolbar;
    ArrayList<Song> songsToDisplay;
    private Integer code;
    private static Integer currentPosPressed;
    private static String whichplaylist ="";
    private  ListView listView;
    private RelativeLayout layoutForMediaController;
    private ImageButton start;
    private ImageButton go_next_song;
    private ImageButton go_previous_song;
    private TextView songName;
    private boolean isBound=false;
    private MusicService myservice=null;
    private boolean pressedAtLeastOneSongInThePlaylist=false;
    private  boolean isActivityVisible=false;
    private BroadcastReceiver receiverForMediaController=new BroadcastReceiver() {
        @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().matches("projects.boldurbogdan.actionUpdateStatus")) {
                    if (isActivityVisible) {
                        Toast.makeText(getApplicationContext(), "i have received the update for mediaController", Toast.LENGTH_SHORT).show();
                        ArrayList<String> infoToUpdate = myservice.giveInfoToCurrentActivity();
                        songName.setText(infoToUpdate.get(1), TextView.BufferType.EDITABLE);
                        currentPosPressed = myservice.songPositionToBePlayed;
                        if (myservice.mp.isPlaying()) {

                            start.setBackground(getResources().getDrawable(R.drawable.pause));

                        } else {
                            start.setBackground(getResources().getDrawable(R.drawable.play));
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
        bindToService();
    }

   @Override
    protected void onStop() {
       super.onStop();
       isActivityVisible=false;
       myservice.setActivityState("PlaylistSongs",false);

   }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiverForMediaController);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiverForStoppingActivity);
        unbindFromSerivce();
        isBound=false;
        myservice=null;
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_playlist_songs);

        listView = (ListView) findViewById(R.id.activityShowSongsPlaylist);
        layoutForMediaController= (RelativeLayout) findViewById(R.id.include3);
        toolbar= (Toolbar) findViewById(R.id.toolbarForPlaylistSongs);
        setSupportActionBar(toolbar);

        SetupMediaControllerButtons();
        Intent i = getIntent();
        code = i.getIntExtra("code", -1);
        currentPosPressed=i.getIntExtra("positionFromOtherActiities",0);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiverForStoppingActivity,new IntentFilter("projects.boldurbogdan.CLOSEACTIVITIES"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiverForMediaController,new IntentFilter("projects.boldurbogdan.actionUpdateStatus"));
        if (code == -1) {
            Log.i("code", String.valueOf(-1));
        } else if (code == 1 || code == 3) {
            whichplaylist = i.getStringExtra("nameOfPlaylist");
        } else if (code == 2) {
            whichplaylist = i.getStringExtra("name_of_playlist");
        }

        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        songsToDisplay = helper.retrieveSongsForSpecificPlaylist(whichplaylist);
        SongAdapter adapter = new SongAdapter(getApplicationContext(), songsToDisplay);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!pressedAtLeastOneSongInThePlaylist) {
                    pressedAtLeastOneSongInThePlaylist = true;
                    myservice.InitiateSongArray(songsToDisplay, whichplaylist);
                }

              if(myservice.songPositionToBePlayed==-1 || position==myservice.songPositionToBePlayed){
                  myservice.playSong(position);
                  Intent i=new Intent(PlaylistSongs.this,CurrentlyPlayingActivity.class);
                  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                  startActivity(i);

              }
                else{
                  if(position!=myservice.songPositionToBePlayed){
                      myservice.playSong(position);
                      Intent i=new Intent(PlaylistSongs.this,CurrentlyPlayingActivity.class);
                      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                      startActivity(i);
                  }

              }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlaylistSongs.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    private void SetupMediaControllerButtons() {
        start = (ImageButton) layoutForMediaController.findViewById(R.id.play);
        go_next_song = (ImageButton) layoutForMediaController.findViewById(R.id.go_next_song);
        go_previous_song = (ImageButton) layoutForMediaController.findViewById(R.id.go_previous_song);
        songName = (TextView) layoutForMediaController.findViewById(R.id.songPlaying);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myservice.songPositionToBePlayed==-1) {
                    Toast.makeText(getApplicationContext(), "nothing was selected", Toast.LENGTH_SHORT).show();
                } else {

                    if (myservice.mp.isPlaying()) {
                        myservice.pauseSong();
                        start.setBackground(getResources().getDrawable(R.drawable.play));

                    } else {
                        Log.i("current pos", String.valueOf(currentPosPressed));
                        myservice.resumePlaying(myservice.songPositionToBePlayed);
                        start.setBackground(getResources().getDrawable(R.drawable.pause));

                    }
                }

            }
        });
        go_next_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myservice.mp != null) {
                    start.setBackground(getResources().getDrawable(R.drawable.pause));
                    myservice.playNextSong(myservice.songPositionToBePlayed);
                    songName.setText(myservice.getCurrentlyPlayingSong(),TextView.BufferType.EDITABLE);
                }
            }
        });
        go_previous_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myservice.mp != null) {
                    start.setBackground(getResources().getDrawable(R.drawable.pause));
                    myservice.playNextSong(myservice.songPositionToBePlayed);
                    songName.setText(myservice.getCurrentlyPlayingSong(), TextView.BufferType.EDITABLE);
                }
            }
        });
    }
    private void unbindFromSerivce(){
        myservice.setServiceRunning(false);
        isBound=false;
        unbindService(myConnToMusicService);
        myservice=null;
    }
    private void bindToService(){
        if(isBound==false)
        {
            Intent bindIntent=new Intent(this,MusicService.class);
            bindService(bindIntent,myConnToMusicService,Context.BIND_AUTO_CREATE);
            isBound=true;
        }
    }
   ServiceConnection myConnToMusicService=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder=(MusicService.LocalBinder)service;
            myservice=binder.getService();
            isBound=true;
            checkStateOfActivityAndService();


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myservice=null;
            isBound=false;
        }

    public void checkStateOfActivityAndService(){
        if(myservice.returnNameOfPlaylistCurrentlyPlayed().equals("null")){
            myservice.InitiateSongArray(songsToDisplay,whichplaylist);
            myservice.setCurrentlyPlaylistPlaying(whichplaylist);
        }
        else{
            if(!myservice.returnNameOfPlaylistCurrentlyPlayed().equals(whichplaylist) &&pressedAtLeastOneSongInThePlaylist){
                myservice.InitiateSongArray(songsToDisplay,whichplaylist);
                myservice.setCurrentlyPlaylistPlaying(whichplaylist);
            }
        }

        try {
           if(myservice.isServiceRunning()==false)
           {
                myservice.setServiceRunning(true);
                Toast.makeText(getApplicationContext(),"now it is running",Toast.LENGTH_SHORT).show();
            }
            if (myservice.getActivityState("PlaylistSongs")==false)
            {
                myservice.setActivityState("PlaylistSongs",true);
                myservice.updateNotification();
                isActivityVisible=true;
                Toast.makeText(getApplicationContext(),"Activity PlaylistSongs was not visible until now",Toast.LENGTH_SHORT).show();
                ArrayList<String> infoToUpdate = myservice.giveInfoToCurrentActivity();
                songName.setText(infoToUpdate.get(1), TextView.BufferType.EDITABLE);
                if (myservice.mp.isPlaying()) {
                    start.setBackground(getResources().getDrawable(R.drawable.pause));

                } else {
                    start.setBackground(getResources().getDrawable(R.drawable.play));
                }

            }
        }catch (Exception e){
            e.toString();
        }

    }
};
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_playlist,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.ADD:
                Intent intentAdd=new Intent(this,SelectSongsForPLaylist.class);
                intentAdd.putExtra("name_of_playlist",whichplaylist);
                startActivity(intentAdd);
                break;
            case R.id.REMOVE:
                Intent intentRemove=new Intent(this,DeleteSongsFromPlaylist.class);
                intentRemove.putExtra("name_of_playlist",whichplaylist);
                startActivity(intentRemove);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
    return false;}

}
