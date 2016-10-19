package projects.boldurbogdan.mymediaplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by boldurbogdan on 17/09/2016.
 */
public class PlaylistAdapterForAlertDialog extends BaseAdapter {
    private ArrayList<Playlist> playlists;
    private Context c;
    private LayoutInflater inflater;

    public PlaylistAdapterForAlertDialog(Context context,ArrayList<Playlist>playlistArrayList){
        this.c=context;
        this.inflater=LayoutInflater.from(c);
        this.playlists=playlistArrayList;
    }
    @Override
    public int getCount() {
        return playlists.size();
    }

    @Override
    public Object getItem(int position) {
        return playlists.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return playlists.get(position).hashCode();
    }

    private class ViewHolder{
        private TextView nameOfThePlaylist;
        public ViewHolder(View rowToHold){
            this.nameOfThePlaylist= (TextView) rowToHold.findViewById(R.id.NameOfThePlaylistForAlertDialog);
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View current_row=convertView;
        ViewHolder holder;
        if(current_row==null){
            current_row=inflater.inflate(R.layout.playlist_layout_for_alert_dialog,null);
            holder=new ViewHolder(current_row);
            current_row.setTag(holder);
        }
        else{
            holder= (ViewHolder) current_row.getTag();
        }
        holder.nameOfThePlaylist.setText(playlists.get(position).getName());
    return current_row;
    }
}
