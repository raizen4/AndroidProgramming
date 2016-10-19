package projects.boldurbogdan.mymediaplayer;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private ListView listviewForYoutube;
    private SearchView searchViewForYoutube;
    ArrayList<YoutubeObject> youtubeObjectArrayList = new ArrayList<>();
    Handler mHandler=new Handler();
    Request request=null;
    final OkHttpClient client = new OkHttpClient();

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listviewForYoutube = (ListView) getActivity().findViewById(R.id.listviewForSongsRetrievedFromYoutube);
        searchViewForYoutube = (SearchView) getActivity().findViewById(R.id.youtubeSearchSong);
        searchViewForYoutube.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String formattedQuery=query.replaceAll(" ","+");
                Request request = new Request.Builder()
                        .url("https://www.googleapis.com/youtube/v3/search?part=snippet&q="+formattedQuery+"&type=video&key=AIzaSyAgE6MDjFPXD1eWnU4-MwAo_VVVQMk7gs0")
                        .build();
                makeRequest(request);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        final YoutubeVideoAdapter adapter = new YoutubeVideoAdapter(getActivity().getApplicationContext(), youtubeObjectArrayList);
        listviewForYoutube.setAdapter(adapter);
    }
    private void makeRequest(Request request){
        if (request != null) {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(youtubeObjectArrayList.size()!=0){
                        youtubeObjectArrayList.clear();
                    }
                    String dataFromYoutube = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(dataFromYoutube);
                        Log.i("dateDespreYoutube", jsonObject.toString());
                        JSONArray array = jsonObject.getJSONArray("items");
                        for (int i = 0; i < array.length(); ++i) {
                            JSONObject currentItem = array.getJSONObject(i);
                            JSONObject idOfTheVideo = currentItem.getJSONObject("id");
                            JSONObject snippetOfTheObject = currentItem.getJSONObject("snippet");
                            JSONObject thumbnails = (JSONObject) snippetOfTheObject.get("thumbnails");
                            JSONObject mediumThumbnail = thumbnails.getJSONObject("medium");
                            String videoID = idOfTheVideo.getString("videoId");
                            String name = snippetOfTheObject.getString("title");
                            String thumbnailURL = mediumThumbnail.getString("url");
                            YoutubeObject item = new YoutubeObject(name, thumbnailURL, videoID);
                            Log.i("object", item.getName() + item.getVideoId());
                            youtubeObjectArrayList.add(item);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((YoutubeVideoAdapter) listviewForYoutube.getAdapter()).notifyDataSetChanged();
                                }
                            });


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


        }

    }
}