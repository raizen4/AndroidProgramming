package projects.boldurbogdan.newsapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    EditText text_field;
    Button button;
    CharSequence text_entered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
        text_field=(EditText) findViewById(R.id.text_entered);
        button=(Button)findViewById(R.id.button);


    }
    public void Click_show(View v){
        text_entered=(text_field.getText().toString().replaceAll(" ","%20"));
        Log.i("text_entered:", (String) text_entered);
        Intent i=new Intent(getApplicationContext(),SecondActivity.class);
        i.putExtra("query_text", text_entered);
         startActivity(i);



    }
}
