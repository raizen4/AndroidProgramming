package projects.boldurbogdan.mymediaplayer;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static android.R.attr.id;

/**
 * Created by boldurbogdan on 20/09/2016.
 */
public class YoutubeVideoAdapter extends BaseAdapter {
    private ArrayList<YoutubeObject> youtubeObjectArrayList;
    private Context c;
    private LayoutInflater inflater=null;
    MediaPlayer player=new MediaPlayer();
    private int currentPosPressed;
    int progress;
    private final OkHttpClient client=new OkHttpClient();



    private class ViewHolder{
        ImageView imageOfTheVideo;
        TextView nameOfTheVideo;
        ImageButton previewTheVideo;
        ImageButton downloadVideo;
        public ViewHolder(View current_row){
            imageOfTheVideo= (ImageView) current_row.findViewById(R.id.photoOfTheSongYoutube);
            nameOfTheVideo= (TextView) current_row.findViewById(R.id.nameOfTheSongYoutube);
            previewTheVideo= (ImageButton) current_row.findViewById(R.id.playSongYoutube);
            downloadVideo= (ImageButton) current_row.findViewById(R.id.downloadButton);

        }
    }
    public YoutubeVideoAdapter(Context context,ArrayList<YoutubeObject> arrayList){
        this.c=context;
        this.youtubeObjectArrayList=arrayList;
        inflater=LayoutInflater.from(c);
    }
    @Override
    public int getCount() {
      return youtubeObjectArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return youtubeObjectArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return youtubeObjectArrayList.get(position).hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View currentRow=convertView;
        final ViewHolder holder;
        currentPosPressed=position;
        if(currentRow==null){
            currentRow=inflater.inflate(R.layout.row_for_youtube_video,null);
            holder=new ViewHolder(currentRow);
            currentRow.setTag(holder);
        }
        else{
            holder= (ViewHolder) currentRow.getTag();
        }
        Picasso.with(c).load(Uri.parse(youtubeObjectArrayList.get(position).getPhoto())).placeholder(R.drawable.music_photo).resize(200,200).into(holder.imageOfTheVideo);
        holder.nameOfTheVideo.setText(youtubeObjectArrayList.get(position).getName());
        holder.downloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.previewTheVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeObjectArrayList.get(position).getVideoId()));
                    appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + youtubeObjectArrayList.get(position).getVideoId()));
                webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        c.startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        c.startActivity(webIntent);
                    }

            }
        });
        holder.downloadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               okhttp3.Request request = new okhttp3.Request.Builder()
                       .url("http://www.youtubeinmp3.com/fetch/?format=JSON&maxResults=15&video=http://www.youtube.com/watch?v="+youtubeObjectArrayList.get(position).getVideoId())
                       .build();
                client.newCall(request).enqueue(new Callback() {
                   @Override
                   public void onFailure(Call call, IOException e) {

                   }

                   @Override
                   public void onResponse(Call call, Response response) throws IOException {
                       JSONObject objectReceived=null;
                       try {
                              objectReceived=new JSONObject(response.body().string());
                               Log.i("secondObjectReceived",objectReceived.toString());
                                String urlForDownload= (String) objectReceived.get("link");
                               Log.i("urlForRealDownload",urlForDownload.toString());
                           DownloadManager.Request requestForDownload=new DownloadManager.Request(Uri.parse(urlForDownload));
                                requestForDownload.allowScanningByMediaScanner();
                                requestForDownload.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                requestForDownload.setTitle(youtubeObjectArrayList.get(position).getName()+ "Downloading");
                                requestForDownload.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,youtubeObjectArrayList.get(position).getName()+".mp3");
                           DownloadManager manager= (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
                           manager.enqueue(requestForDownload);
                       } catch (JSONException e) {
                           e.printStackTrace();

                       }
                   }
               });
            }
        });
    return currentRow;
    }
}


