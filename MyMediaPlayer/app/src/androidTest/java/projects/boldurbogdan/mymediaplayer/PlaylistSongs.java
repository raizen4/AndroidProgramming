package projects.boldurbogdan.mymediaplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

class PlaylstSongs extends AppCompatActivity{
    private MediaPlayer mp=null;
    private boolean activityisPaused=false;
    private  Bundle savecurrentinstance=null;
    private ArrayList<Song> songsToDisplay;
    private int lenght=0;
    private Integer code;
    private Handler handler=new Handler();
    private Integer currentPosPressed=0;
    private String whichplaylist ="";
    private  ListView listView;
    private  View layoutForMediaController;
    private ImageButton start;
    private ImageButton go_next_song;
    private ImageButton go_previous_song;
    private SeekBar songProgressionBar;
    private TextView songName;

    private boolean isBound=false;
    private MusicService myservice=null;
    private ServiceConnection myConnToMusicService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savecurrentinstance!=null){


        }
        myConnToMusicService=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.LocalBinder binder=(MusicService.LocalBinder)service;
                myservice=binder.getService();
                isBound=true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound=false;
            }
        };
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_songs);

        Intent i = getIntent();
        code = i.getIntExtra("code", -1);
        if (code == -1) {
            Log.i("code", String.valueOf(-1));
        } else if (code == 1 || code == 3) {
            whichplaylist = i.getStringExtra("nameOfPlaylist");
        } else if (code == 2) {
            whichplaylist = i.getStringExtra("name_of_playlist");
        }

        listView = (ListView) findViewById(R.id.activityShowSongsPlaylist);
        layoutForMediaController=findViewById(R.id.mediacontroller);

        SetupMediaControllerButtons();

        DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
        songsToDisplay = helper.retrieveSongsForSpecificPlaylist(whichplaylist);
        SongAdapter adapter = new SongAdapter(getApplicationContext(), songsToDisplay);
        listView.setAdapter(adapter);

        Runnable updateseekbar=new Runnable() {
            @Override
            public void run() {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              if(mp==null){
                  currentPosPressed=position;
                  playSong(position);

              }
                else{
                  if(position!=currentPosPressed){
                      mp.release();
                      currentPosPressed=position;
                      playSong(position);

                  }
                  else if(position==currentPosPressed &&mp.isPlaying()==false){
                      try {
                          resumePlaying(currentPosPressed);
                          start.setBackground(getResources().getDrawable(R.drawable.pause));
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
                  else{
                      pauseSong();
                  }

              }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }

        });


    }

    @Override
    public void onBackPressed() {
        mp.release();
        mp=null;
        Intent intent = new Intent(this, MainActivity.class);
        freeMemory();
        startActivity(intent);
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
    private void SetupMediaControllerButtons(){
         start=(ImageButton) layoutForMediaController.findViewById(R.id.play);
         go_next_song=(ImageButton) layoutForMediaController.findViewById(R.id.go_next_song);
         go_previous_song=(ImageButton) layoutForMediaController.findViewById(R.id.go_previous_song);
         songName=(TextView)layoutForMediaController.findViewById(R.id.songPlaying);
         songProgressionBar=(SeekBar)layoutForMediaController.findViewById(R.id.barForTheSongCompletion);
         start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(mp==null){
                   Toast.makeText(getApplicationContext(),"nothing was selected",Toast.LENGTH_SHORT).show();
               }
                else {
                   if (mp.isPlaying()) {
                       pauseSong();
                   } else {
                       Log.i("current pos", String.valueOf(currentPosPressed));
                       try {
                           resumePlaying(currentPosPressed);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               }

            }
        });
        go_next_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp!=null) {
                    playNextSong(currentPosPressed);
                }
            }
        });
        go_previous_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp!=null){
                    playPreviousSong();
                }
            }
        });
    }
    private void pauseSong(){
        lenght=mp.getCurrentPosition();
        mp.pause();
        start.setBackground(getResources().getDrawable(R.drawable.play));
    }
    private void playPreviousSong(){

    }
    private void playSong(int currentpos){
        songName.setText(songsToDisplay.get(currentpos).getName());
        start.setBackground(getResources().getDrawable(R.drawable.pause));
    }

    private void resumePlaying(int pos) throws IOException {

        start.setBackground(getResources().getDrawable(R.drawable.pause));
    }
    private void playNextSong(int position){
        if(position+1>songsToDisplay.size()-1){
            currentPosPressed=0;
            mp.stop();
            mp.release();
            mp=null;
            mp=new MediaPlayer();
            try { songName.setText(songsToDisplay.get(currentPosPressed).getName());
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(this,Uri.parse(songsToDisplay.get(currentPosPressed).getPath()));
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            start.setBackground(getResources().getDrawable(R.drawable.pause));
            mp.start();

        }
        else{
            mp.stop();
            mp.release();
            mp=null;
            mp=new MediaPlayer();
            currentPosPressed=position+1;
            try {
                songName.setText(songsToDisplay.get(currentPosPressed).getName());
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setDataSource(this,Uri.parse(songsToDisplay.get(currentPosPressed).getPath()));
                mp.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            start.setBackground(getResources().getDrawable(R.drawable.pause));
            mp.start();


        }
    }

    @Override
    protected void onDestroy() {
        mp.release();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityisPaused=true;
        savecurrentinstance=new Bundle();
        String currentlyPlayedSong;
        int currentPositionInSong;
        int positionOfTheCurrentSongInList;
        if(mp.isPlaying()){
            currentlyPlayedSong=songName.getText().toString();
            currentPositionInSong=mp.getCurrentPosition();
            positionOfTheCurrentSongInList=currentPosPressed;
            savecurrentinstance.putString("currentlyPlayedSong",currentlyPlayedSong);
            savecurrentinstance.putInt("currentPositionInSong",currentPositionInSong);
            savecurrentinstance.putInt("positionOfTheCurrentSongInList",positionOfTheCurrentSongInList);
        }

    }


}