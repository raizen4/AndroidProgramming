package projects.boldurbogdan.mymediaplayer;

import android.animation.Animator;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class SelectSongsForPLaylist extends AppCompatActivity {
    ListView listView;
    final Integer code=3;
    SearchView searchView;
    ArrayList<Song> songsToBeDisplayed=new ArrayList<>();
    ArrayList<Song> songsToAdd=new ArrayList<>();
    Button but_finish;
    Button but_cancel;
    String playlistname;
    final public static Uri sArtworkUri = Uri
            .parse("content://media/external/audio/albumart");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_for_playlist);
        lookformusic(songsToBeDisplayed);
        final Intent intent=getIntent();
        final  View viewForFinishingAddingSongs=findViewById(R.id.view_for_finishing_adding_songs);
        playlistname=intent.getStringExtra("name_of_playlist");
        Log.i("name",playlistname);
        but_finish = (Button)viewForFinishingAddingSongs.findViewById(R.id.button_finish_adding_songs);
        but_cancel = (Button)viewForFinishingAddingSongs.findViewById(R.id.button_cancel_adding_songs);
        final Animation fadeinanim=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        listView = (ListView) findViewById(R.id.listview_for_adding_items_to_playlist);
        SongAdapterCheckable adapter = new SongAdapterCheckable(getApplicationContext(),songsToBeDisplayed);
        listView.setAdapter(adapter);
        if(songsToAdd.size()>0){
            fadeinanim.setStartOffset(2000);
            viewForFinishingAddingSongs.setAnimation(fadeinanim);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if( songsToBeDisplayed.get(position).isSelected()){
                    songsToBeDisplayed.get(position).setSelected(false);
                    songsToAdd.remove(songsToBeDisplayed.get(position));
                }
                else {
                    songsToBeDisplayed.get(position).setSelected(true);
                    songsToAdd.add(songsToBeDisplayed.get(position));
                }
                SongAdapterCheckable.Viewholder holder= (SongAdapterCheckable.Viewholder) view.getTag();
                holder.checkBox.setChecked(songsToBeDisplayed.get(position).isSelected());
            }
        });
        but_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfSongsToAdd=0;
                DatabaseHelper helper = new DatabaseHelper(getApplicationContext());
                        for (int i = 0; i < songsToAdd.size(); ++i) {
                            if( helper.songAlreadyInPlaylist(songsToAdd.get(i).getId(),playlistname)==0)
                            {   Toast.makeText(getApplicationContext(),"the song"+songsToAdd.get(i).getName()+"is not in the playlist",Toast.LENGTH_SHORT).show();
                              helper.insertSongInPlyalist(songsToAdd.get(i).getId(), playlistname);
                                numberOfSongsToAdd++;
                        }
                        }
                int currentNumberOftracks=helper.getNumberOfTracksForPlaylist(playlistname);
                helper.updateNumberOfTracks(playlistname,numberOfSongsToAdd+currentNumberOftracks);
                Intent intent1=new Intent(SelectSongsForPLaylist.this,PlaylistSongs.class);
                intent1.putExtra("nameOfPlaylist",playlistname);
                intent1.putExtra("code",code);
                startActivity(intent1);
                finish();


            }
        });
        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SelectSongsForPLaylist.this,PlaylistSongs.class);
                intent.putExtra("name_of_playlist",playlistname);
                intent.putExtra("code",2);
                startActivity(intent);
                finish();
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(SelectSongsForPLaylist.this,PlaylistSongs.class);
        intent.putExtra("name_of_playlist",playlistname);
        intent.putExtra("code",2);
        startActivity(intent);
        finish();
    }

    private void lookformusic(ArrayList<Song> itemsToAdd){
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
                Log.i("Name",newsong.getName()+String.valueOf(newsong.getArt())+newsong.getPath());
                itemsToAdd.add(newsong);
                if(itemsToAdd.get(itemsToAdd.size()-1).real_duration_in_minutes<100){
                   itemsToAdd.remove(itemsToAdd.size()-1);}
            } while (musicCursor.moveToNext());
        }
        musicCursor.close();
    }


}