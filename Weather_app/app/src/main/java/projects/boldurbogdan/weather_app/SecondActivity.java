package projects.boldurbogdan.weather_app;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;

public class SecondActivity extends AppCompatActivity {
   static  int temp;
   static  float wind_speed;
    static String main_weather;
   static  String description;
    TextView weather_text;
    TextView temp_text;
    TextView wind_text;
    Button button;
    private static double kelvin_celsius(double kelvin){
      double T_celsius=kelvin-273.15;
        return T_celsius;
    }
    public void Back_button(View view){
        Intent i=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent i=getIntent();
        main_weather=i.getStringExtra("main_weather");
        description=i.getStringExtra("description");
        Log.i("weather", main_weather);
        Log.i("descr",description);
        temp= (int) kelvin_celsius(i.getDoubleExtra("temp",-1));
        wind_speed=(float)i.getDoubleExtra("wind_speed",0);
        weather_text= (TextView) findViewById(R.id.text_weather);
        String weather_to_be_shown=main_weather+":"+description;
        weather_text.setText(weather_to_be_shown, TextView.BufferType.EDITABLE);
        temp_text=(TextView)findViewById(R.id.temp_text);
        temp_text.setText(Integer.toString(temp)+" C", TextView.BufferType.EDITABLE);
        wind_text=(TextView)findViewById(R.id.wind_speed_field);
        wind_text.setText("Wind speed: "+String.valueOf(wind_speed+" KP/H"), TextView.BufferType.EDITABLE);
    }
}
