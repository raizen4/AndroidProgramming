package projects.boldurbogdan.rssfeed;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText text_field;
    Button button;
    String text_entered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text_field=(EditText) findViewById(R.id.text_entered);
        button=(Button)findViewById(R.id.button);
        text_entered=text_field.getText().toString();
        Log.i("stringgggg",text_entered);


    }
    public void Click_show(View v){

        Log.i("text_entered", text_entered);
        //Intent i=new Intent(getApplicationContext(),SecondActivity.class);
        //i.putExtra("query_text", text_entered);
       // startActivity(i);



    }
}
