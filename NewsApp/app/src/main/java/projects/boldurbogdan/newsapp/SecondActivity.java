package projects.boldurbogdan.newsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    static boolean active=false;
    String query_text;
    ArrayList<Header_item_list> items_to_be_desplayed=new ArrayList<Header_item_list>();

    @Override
    protected void onStart() {
        super.onStart();
        active=true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar newActionBar=(Toolbar)findViewById(R.id.toolbar_second_activty);
        setSupportActionBar(newActionBar);
        Intent i=getIntent();
        query_text=i.getStringExtra("query_text");
        JsonParser parser=new JsonParser(items_to_be_desplayed,SecondActivity.this);
        parser.execute("https://ajax.googleapis.com/ajax/services/feed/find?" +"v=1.0&q="+query_text);



    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(active==true)
        {
            menu.removeItem(R.id.Save_for_later_reading);
            menu.removeItem(R.id.Share_button);
            return super.onPrepareOptionsMenu(menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_for_app, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Show_saved_articles:
                Intent i=new Intent(this,SavedArticlesActivity.class);
                startActivity(i);



            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        active=false;
    }
}


