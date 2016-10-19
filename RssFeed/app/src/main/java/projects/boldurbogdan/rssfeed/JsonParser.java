package projects.boldurbogdan.rssfeed;

/**
 * Created by boldurbogdan on 12/06/2016.
 */
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by boldurbogdan on 10/06/2016.
 */

public class JsonParser extends AsyncTask<String,Void,String> {
    private ArrayList<Header_item_list> items;

    public JsonParser(ArrayList<Header_item_list>new_items){
        this.items=new_items;
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
    protected void onPostExecute(String website) {
        super.onPostExecute(website);
        try {
            JSONObject obj=new JSONObject(website);
            JSONObject response_data=  (JSONObject) obj.get("responseData");
            JSONArray entries= (JSONArray) response_data.get("entries");
            for (int i=0;i<entries.length();++i){
                JSONObject entry = (JSONObject) entries.get(i);
                String title=entry.getString("title");
                String website_url=entry.getString("link");
                ImageUrlGenerator get_first_img=new ImageUrlGenerator(website);
                String first_image_url=get_first_img.download();
                Header_item_list header_item_list =new Header_item_list(title,first_image_url);
                items.add(header_item_list);



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
