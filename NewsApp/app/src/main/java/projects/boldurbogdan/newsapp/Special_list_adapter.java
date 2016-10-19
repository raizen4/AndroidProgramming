package projects.boldurbogdan.newsapp;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by boldurbogdan on 13/06/2016.
 */
    public class Special_list_adapter extends BaseAdapter {
        private ArrayList<Header_item_list> items;
        Context context;
        LayoutInflater inflater=null;

        public Special_list_adapter(ArrayList<Header_item_list> list, Context c) {
            this.items=list;
            this.context=c;
            inflater=LayoutInflater.from(context);
        }

        static class ViewHolder {
            TextView title;
            ImageView img;
            public ViewHolder(View current_item_on_row){
                this.title=(TextView) current_item_on_row.findViewById(R.id.header_text);
                this.img= (ImageView) current_item_on_row.findViewById(R.id.header_img);


            }

        }

        @Override
        public int getCount() {
            return  items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return items.get(position).hashCode();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View current_row=convertView;
            ViewHolder holder;
            if (current_row==null) {
                current_row=inflater.inflate(R.layout.list_layout,null);
                holder=new ViewHolder(current_row);
                current_row.setTag(holder);

            }
            else{
                holder=(ViewHolder) current_row.getTag();
            }
            holder.title.setText(Html.fromHtml((String) items.get(position).getTitle()));
            try{
                Picasso.with(context).
                        load(items.get(position).
                                getImgUrl()).
                        error(R.drawable.android_logo).
                        into(holder.img);
            }
            catch(Exception e){
                Log.e("eror with picasso",items.get(position).getImgUrl().toString());
            }
            return current_row;}
    }



