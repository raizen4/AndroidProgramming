package projects.boldurbogdan.mymediaplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SongsFragment.listener {

    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    private RelativeLayout layoutForMediaController;
    protected ImageButton start;
    protected ImageButton go_next_song;
    protected ImageButton go_previous_song;
    protected TextView songName;
    protected  boolean  pressedAtLeastOneSongInThePlaylistInMainActivity=false;
    private boolean isBound=false;
    private  boolean isActivityVisible=false;
    protected static  int currentPosPressedInMainActivity=0;
    protected MusicService myservice=null;
    protected  ArrayList<Song> mysongs=new ArrayList<>();
    final public static Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");
    private BroadcastReceiver receiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches("projects.boldurbogdan.actionUpdateStatus")) {
                if (isActivityVisible) {
                    ArrayList<String> infoToUpdate = myservice.giveInfoToCurrentActivity();
                    songName.setText(infoToUpdate.get(1), TextView.BufferType.EDITABLE);
                    if (myservice.mp.isPlaying()) {

                        start.setBackground(getResources().getDrawable(R.drawable.pause));

                    } else {
                        start.setBackground(getResources().getDrawable(R.drawable.play));
                    }

                }
            }
        }
    };;
    private BroadcastReceiver receiverForMediaController=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().matches("projects.boldurbogdan.ACTION_UPDATE_MEDIA_CONTROLLER")) {
                    Toast.makeText(getApplicationContext(), "i have received the update for mediaController", Toast.LENGTH_SHORT).show();
                    ArrayList<String> infoToUpdate = myservice.giveInfoToCurrentActivity();
                    songName.setText(infoToUpdate.get(1), TextView.BufferType.EDITABLE);
                    currentPosPressedInMainActivity = myservice.songPositionToBePlayed;
                    if (myservice.mp.isPlaying()) {

                        start.setBackground(getResources().getDrawable(R.drawable.pause));

                    } else {
                        start.setBackground(getResources().getDrawable(R.drawable.play));
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
    protected void onStop() {
        super.onStop();
        isActivityVisible=false;
        myservice.setActivityState("MainPlayingActivity",false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(),"callled on destory",Toast.LENGTH_SHORT).show();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiverForMediaController);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiverForStoppingActivity);
        unBindFromService();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiverForMediaController,new IntentFilter("projects.boldurbogdan.actionUpdateStatus"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiverForMediaController,new IntentFilter("projects.boldurbogdan.ACTION_UPDATE_MEDIA_CONTROLLER"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiverForStoppingActivity,new IntentFilter("projects.boldurbogdan.CLOSEACTIVITIES"));
        bindToService();

    }
    private void bindToService(){
        if(!isBound) {
            Intent i = new Intent(this, MusicService.class);
            bindService(i, myMusicConnection, BIND_AUTO_CREATE);
        }
    }
    private void unBindFromService(){
        myservice.setServiceRunning(false);
        isBound=false;
        unbindService(myMusicConnection);
        myservice=null;
        isActivityVisible=false;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (weHavePermissionSD()) {
            lookformusic();

        }

        else {
            requestReadSDPermissionFirst();
        }
        if(weHavePermissionWriteOnSd()==false){
            requestWriteSDPermissionFirst();
        }
        toolbar= (Toolbar) findViewById(R.id.tooblar);
        setSupportActionBar(toolbar);
        tabLayout= (TabLayout) findViewById(R.id.tablayout);
        ViewPager viewPager= (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.add_frag(new SearchFragment(),"Search");
        pagerAdapter.add_frag(new SongsFragment(),"Songs");
        pagerAdapter.add_frag(new PlaylistFragment(),"Playlist");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        layoutForMediaController= (RelativeLayout) findViewById(R.id.music_controller);
        SetupMediaControllerButtons();


    }

ServiceConnection myMusicConnection=new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.LocalBinder binder= (MusicService.LocalBinder) service;
        myservice=binder.getService();
        isBound=true;
        checkStateOfActivity();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isBound=false;
        myservice=null;
    }
    public void checkStateOfActivity(){
        if(myservice.returnNameOfPlaylistCurrentlyPlayed().equals("null")){
            myservice.InitiateSongArray(mysongs,"ALL");
        }
        else{
            if( !myservice.returnNameOfPlaylistCurrentlyPlayed().equals("ALL")){
                myservice.InitiateSongArray(mysongs,"ALL");
                myservice.setCurrentlyPlaylistPlaying("ALL");

            }
        }

        try {
            if(myservice.isServiceRunning()==false){
                myservice.setServiceRunning(true);
                Toast.makeText(getApplicationContext(),"the service is running",Toast.LENGTH_SHORT).show();
            }

            if (myservice.getActivityState("MainPlayingActivity")==false) {
                myservice.setActivityState("MainPlayingActivity",true);
                isActivityVisible=true;
                myservice.updateNotification();
                Toast.makeText(getApplicationContext(),"Activity main was not visible until now",Toast.LENGTH_SHORT).show();
                currentPosPressedInMainActivity=myservice.songPositionToBePlayed;

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
    private boolean weHavePermissionSD() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;}

    private boolean weHavePermissionWriteOnSd(){
        return ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
    }
    private void requestWriteSDPermissionFirst(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},124);
    }
    private void requestReadSDPermissionFirst() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            lookformusic();
        }
        else{
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    protected void lookformusic(){
        ContentResolver musicresolver=getContentResolver();
        Uri musicuri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor=musicresolver.query(musicuri,null,null,null,null);
        if(musicCursor!=null && musicCursor.moveToFirst()) {
            do {
                int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);
                int durationcolumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int albumIdColumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int pathcolumn=musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                Long song_id = musicCursor.getLong(idColumn);
                String song_name = musicCursor.getString(titleColumn);
                String song_artist = musicCursor.getString(artistColumn);
                Long album_id = musicCursor.getLong(albumIdColumn);
                Uri song_photo = ContentUris.withAppendedId(sArtworkUri, album_id);
                Long duration = musicCursor.getLong(durationcolumn);
                String path=musicCursor.getString(pathcolumn);
                Song newsong = new Song(song_id, song_name, song_photo, String.valueOf(duration), song_artist,path);
                mysongs.add(newsong);
                if(mysongs.get(mysongs.size()-1).real_duration_in_minutes<100){
                    mysongs.remove(mysongs.size()-1);
                }
                else{
                    try{
                        DatabaseHelper helper=new DatabaseHelper(getApplicationContext());
                        helper.insertSong(newsong);
                    }
                    catch(Exception e){
                        e.toString();
                    }
                }

            } while (musicCursor.moveToNext());
        }
        musicCursor.close();
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
                        Log.i("current pos", String.valueOf(myservice.songPositionToBePlayed));
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

    @Override
    public void bringBackbool(boolean boolSentBack) {
           pressedAtLeastOneSongInThePlaylistInMainActivity=boolSentBack;
            myservice.InitiateSongArray(mysongs,"ALL");
            myservice.setCurrentlyPlaylistPlaying("ALL");


    }

}
