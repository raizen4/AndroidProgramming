package projects.boldurbogdan.newsapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class WebActivity extends AppCompatActivity {
    private String url;
    WebView site;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        site = (WebView) findViewById(R.id.webView);
        url = i.getStringExtra("url");
        Log.i("url", url);
        site.setWebViewClient(new WebViewClient());
        site.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            site.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            site.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        site.loadUrl((url));
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
            case R.id.Share_button:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, site.getUrl());
                Log.i("current url", site.getUrl());
                startActivity(Intent.createChooser(shareIntent, "Share Using..."));
                break;


            case R.id.Save_for_later_reading:

                final LayoutInflater inflater_for_prompt = getLayoutInflater();
                View pop_message = inflater_for_prompt.inflate(R.layout.prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setView(pop_message);
                 final EditText user_input = (EditText) pop_message.findViewById(R.id.txt_for_saving_article);
                final String[] text_for_saving = new String[1];
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK",null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                final AlertDialog dialog = alertDialogBuilder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog_interface) {
                        Button Button_ok=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                text_for_saving[0] =user_input.getText().toString();
                                if(text_for_saving[0].matches("")){
                                    Toast.makeText(getApplicationContext(),"You must insert a name",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Database_helper helper=new Database_helper(getApplicationContext());
                                    Site_Item item=new Site_Item(text_for_saving[0],site.getUrl());
                                    helper.insert(item);
                                    dialog.dismiss();
                                    ArrayList<Site_Item> new_items=helper.retrieveAllRecords();
                                    for(Site_Item item_d:new_items){
                                        Log.i("name",item.getName());
                                    }
                                }
                            }
                        });

                    }
                });
                dialog.show();
                break;

            case R.id.Show_saved_articles:
                Intent i=new Intent(this,SavedArticlesActivity.class);
               startActivity(i);



            default:
                return super.onOptionsItemSelected(item);
        }
    return true;
    }
}