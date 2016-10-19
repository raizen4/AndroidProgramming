package projects.boldurbogdan.mymediaplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by boldurbogdan on 16/07/2016.
 */
public class PlaylistAdapter extends BaseAdapter {
    private ArrayList<Playlist> playlists;
    private Context c;
    private LayoutInflater inflater=null;

    public PlaylistAdapter(Context context,ArrayList<Playlist>playlists){
        this.c=context;
        this.playlists=playlists;
        inflater=LayoutInflater.from(c);
    }
    private class Viewholder{
        TextView numberOfTracks;
        TextView nameOfPlaylsit;
        ImageView image;
        public Viewholder(View current_row){
            numberOfTracks=(TextView)current_row.findViewById(R.id.playlist_track_number);
            nameOfPlaylsit=(TextView)current_row.findViewById(R.id.name_playlist);
            image=(ImageView)current_row.findViewById(R.id.image_playlist);
        }
    }
    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Object getItem(int position) {
        return playlists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return playlists.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        Viewholder holder;
        if (row==null){
            row=inflater.inflate(R.layout.playlist_row,null);
            holder=new Viewholder(row);
            row.setTag(holder);

        }
        else{
            holder= (Viewholder) row.getTag();
        }
        try {
            holder.nameOfPlaylsit.setText(playlists.get(position).getName());
            holder.numberOfTracks.setText(String.valueOf(playlists.get(position).getNumber_of_tracks()));
        }
        catch(Exception e){
            e.toString();
        }
        try{
            Picasso.with(c).load(R.drawable.playlist_img).into(holder.image);
        }
        catch(Exception e){
            e.toString();
        }
        return row;
    }
}
