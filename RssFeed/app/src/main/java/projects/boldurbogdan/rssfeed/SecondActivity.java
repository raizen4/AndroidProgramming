package projects.boldurbogdan.rssfeed;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    String query_text;
    ArrayList<Header_item_list>items_to_be_desplayed=new ArrayList<Header_item_list>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent i=getIntent();
        query_text=i.getStringExtra("query_text");
        Log.i("query",query_text);
        /*JsonParser parser=new JsonParser(items_to_be_desplayed);
        parser.execute("https://ajax.googleapis.com/ajax/services/feed/find?" +"v=1.0&q="+query_text);
        for (Header_item_list item: items_to_be_desplayed){
            Log.i("name",item.getTitle());
            Log.i("url",item.getImgUrl());
        }
        //ListView items_list=(ListView)findViewById(R.id.my_list);
        //Special_list_adapter adapter=new Special_list_adapter(items_to_be_desplayed,getApplicationContext());
        //items_list.setAdapter(adapter);
       */




    }
}

