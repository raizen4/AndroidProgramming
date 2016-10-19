package projects.boldurbogdan.newsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by boldurbogdan on 13/06/2016.
 */
public class JsonParser extends AsyncTask<String,Void,String> {
    Activity ac;
    ArrayList<Header_item_list> items;
    ProgressDialog dialog;
    boolean has_to_refresh=false;
    ListView items_list;
    public JsonParser(ArrayList<Header_item_list> new_items,Activity activity){
        this.items=new_items;
        this.ac=activity;
        this.items_list=(ListView)ac.findViewById(R.id.my_list);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog=ProgressDialog.show(ac,"Loading","We are getting data for you...",true);

    }

    @Override
    protected String doInBackground(String... urls) {
        String website="";
        URL url;
        HttpURLConnection urlConnection=null;
        try {
            url=new URL(urls[0]);
            urlConnection= (HttpURLConnection) url.openConnection();
            InputStream in=urlConnection.getInputStream();
            InputStreamReader reader=new InputStreamReader(in);
            int data=reader.read();
            while(data!=-1){
                char partial_result=(char) data;
                website+=partial_result;
                data=reader.read();
            }
            Log.i("website",website);
            return website;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;

    }

    @Override
    protected void onPostExecute(final String website) {
        super.onPostExecute(website);
        try {
            JSONObject obj=new JSONObject(website);
            JSONObject response_data=  (JSONObject) obj.get("responseData");
            JSONArray entries= (JSONArray) response_data.get("entries");
            for ( int i = 0; i<entries.length(); ++i){
                JSONObject entry = (JSONObject) entries.get(i);
                String title=entry.getString("title");
                String website_url=entry.getString("link");
                ImageUrlGenerator get_first_img= (ImageUrlGenerator) new ImageUrlGenerator(i,website_url, new ImageUrlGenerator.Async_response() {
                    @Override
                    public void get_response(String output, int position,ListView list_to_update) {
                        String url_for_first_img=output;
                        items.get(position).setImg(output);
                        ((BaseAdapter)items_list.getAdapter()).notifyDataSetChanged();

                    }

                },items_list).execute();

                Header_item_list header_item_list =new Header_item_list(title,"no_photo",website_url);
                items.add(header_item_list);

                    }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
        Special_list_adapter adapter=new Special_list_adapter(items,ac);
        items_list.setAdapter(adapter);
        items_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String website_to_access;
                website_to_access=items.get(position).getWebsite_url();
                Toast.makeText(ac.getApplicationContext(),website_to_access,Toast.LENGTH_LONG).show();
                Intent i=new Intent(ac,WebActivity.class);
                i.putExtra("url",website_to_access);
                ac.startActivity(i);


            }
        });

    }


}


