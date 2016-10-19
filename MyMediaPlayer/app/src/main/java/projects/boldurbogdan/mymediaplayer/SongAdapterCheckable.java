package projects.boldurbogdan.mymediaplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by boldurbogdan on 26/07/2016.
 */
public class SongAdapterCheckable extends BaseAdapter{

    ArrayList<Song> songslist;
    ArrayList<Song>temporarylist;
    Context c;
    LayoutInflater inflater=null;

    public SongAdapterCheckable(Context context,ArrayList<Song>songArrayList){
        this.c=context;
        this.songslist=songArrayList;
        this.temporarylist=songArrayList;
        inflater=LayoutInflater.from(c);
    }

    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                temporarylist=(ArrayList<Song>)results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Song> FilteredList= new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = songslist;
                    results.count = songslist.size();
                }
                else {
                    for (int i = 0; i < songslist.size(); i++) {
                        Song data = songslist.get(i);
                        if (data.getName().toLowerCase().contains(constraint.toString()))  {
                            FilteredList.add(data);
                        }
                    }
                    results.values = FilteredList;
                    results.count = FilteredList.size();
                }
                return results;
            }
        };
        return filter;
    }

    static class Viewholder{
        TextView duration;
        TextView name;
        ImageView artcover;
        TextView Artist;
        CheckBox checkBox;
        public Viewholder(View current_item_on_row) {
            this.name=(TextView)current_item_on_row.findViewById(R.id.name_of_the_song);
            this.name.setSelected(true);
            this.duration=(TextView)current_item_on_row.findViewById(R.id.duration);
            this.artcover=(ImageView)current_item_on_row.findViewById(R.id.image_of_the_song);
            this.Artist=(TextView)current_item_on_row.findViewById(R.id.Artist);
            this.checkBox=(CheckBox)current_item_on_row.findViewById(R.id.checkBox);

        }
    }
    @Override
    public int getCount() {
        return temporarylist.size();
    }

    @Override
    public Object getItem(int position) {
        return temporarylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return temporarylist.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View current_row=convertView;
        Viewholder holder;
        if(current_row==null){
            current_row=inflater.inflate(R.layout.row_for_songs_checkable,null);
            holder=new Viewholder(current_row);
            current_row.setTag(holder);
        }
        else {
            holder = (Viewholder) current_row.getTag();
        }
        holder.name.setText(temporarylist.get(position).getName());
        holder.duration.setText(temporarylist.get(position).getDuration());
        holder.Artist.setText(temporarylist.get(position).getArtist());
        holder.checkBox.setChecked(temporarylist.get(position).isSelected());
            try {

                Picasso.with(c)
                        .load(temporarylist.get(position).getArt())
                        .placeholder(R.drawable.music_photo)
                        .resize(200,200)
                        .into(holder.artcover);
            } catch (Exception e) {
                e.toString();
            }


        return current_row;
    }
}


