package projects.boldurbogdan.newsapp;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by boldurbogdan on 13/06/2016.
 */
public class ImageUrlGenerator extends AsyncTask<String,Void,String>
{       private int position;
        private String img_url;
        private Async_response delegate=null;
        private ListView list;

        public ImageUrlGenerator(int position,String url_for_download,Async_response response,ListView list_to_update){
            this.img_url=url_for_download;
            this.delegate=response;
            this.position=position;
            this.list=list_to_update;
        }

     public interface Async_response{
      void get_response(String output, int position,ListView listView);


     }

    @Override
    protected String doInBackground(String... params) {
        Document doc = null;
        try {
            doc = Jsoup.connect(img_url).get();
            Element img=doc.getElementsByTag("img").first();
            String src=img.absUrl("src");

            return src;
        } catch (Exception e) {
            Log.e("problem here:",img_url);


        }

        return null;
    }

    @Override
    protected void onPostExecute(String src) {
        super.onPostExecute(src);
        delegate.get_response(src,position,list);

    }
}


