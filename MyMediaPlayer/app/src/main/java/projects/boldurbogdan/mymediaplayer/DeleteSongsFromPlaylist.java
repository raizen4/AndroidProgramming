package projects.boldurbogdan.mymediaplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeleteSongsFromPlaylist extends AppCompatActivity {
    ListView listviewForRemovingSongs;
    Button button_remove;
    Button button_cancel;
    String playlistName;
    ArrayList<Song> songs;
    ArrayList<Song>songsToRemove=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_songs_from_playlist);
        Intent intent=getIntent();
        playlistName=intent.getStringExtra("name_of_playlist");
        listviewForRemovingSongs= (ListView) findViewById(R.id.listviewForDeletingSongsFromPlaylist);
        View footerView=findViewById(R.id.footerViewOfRemovingSongsActivity);
        button_remove= (Button) footerView.findViewById(R.id.button_finish_adding_songs);
        button_cancel= (Button) footerView.findViewById(R.id.button_cancel_adding_songs);
        final DatabaseHelper helper=new DatabaseHelper(getApplicationContext());
        songs=helper.retrieveSongsForSpecificPlaylist(playlistName);
        SongAdapterCheckable adapterCheckable=new SongAdapterCheckable(getApplicationContext(),songs);
        listviewForRemovingSongs.setAdapter(adapterCheckable);
        listviewForRemovingSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(songs.get(position).isSelected()){
                    songs.get(position).setSelected(false);
                    songsToRemove.remove(songs.get(position));

                }
                else{
                    songs.get(position).setSelected(true);
                    songsToRemove.add(songs.get(position));
                }
                SongAdapterCheckable.Viewholder holder= (SongAdapterCheckable.Viewholder) view.getTag();
                holder.checkBox.setChecked(songs.get(position).isSelected());
            }
        });
    button_cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i=new Intent(DeleteSongsFromPlaylist.this,PlaylistSongs.class);
            i.putExtra("name_of_playlist",playlistName);
            i.putExtra("code",2);
            startActivity(i);
        }
    });
        button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfSongsToRemove=0;
                for(int i=0;i<songsToRemove.size();++i){
                    helper.deleteSongFromPlaylist(songsToRemove.get(i).getId(),playlistName);
                    numberOfSongsToRemove++;

                }
                helper.updateNumberOfTracks(playlistName,helper.getNumberOfTracksForPlaylist(playlistName)-numberOfSongsToRemove);
                Intent i=new Intent(DeleteSongsFromPlaylist.this,PlaylistSongs.class);
                i.putExtra("name_of_playlist",playlistName);
                i.putExtra("code",2);
                startActivity(i);
            }
        });
    }
}
