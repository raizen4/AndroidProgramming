package projects.boldurbogdan.weather_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    EditText location_field;
    static double temp;
    static double wind_speed;
    static String data_received="";
    static String weather_main="";
    static  String weather_description="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location_field=(EditText) findViewById(R.id.location_field);


    }


    public void Show_Weather(View view) {
      String location_text=location_field.getText().toString();
        Log.i("text for location",location_text);
        if (location_text.isEmpty()){
            Toast.makeText(getApplicationContext(),"You have not entered the city,please enter it first",Toast.LENGTH_LONG).show();
        }
        else{

            DownloadTask data_to_download=new DownloadTask();
            data_to_download.execute("http://api.openweathermap.org/data/2.5/weather?q="+location_text+"&appid=2d566e405709b022b5a91130121e1f38");

        }

    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection url_connection=null;
            String result="";

            try{
                url=new URL(urls[0]);
                url_connection= (HttpURLConnection) url.openConnection();
                InputStream in=url_connection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while(data!=-1){
                    char data_read=(char) data;
                    result+=data_read;
                    data=reader.read();
                }
                Log.i("data",result);
                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            data_received=result;
            try{

                JSONObject object_retrieved=new JSONObject(result);
                JSONObject main= (JSONObject) object_retrieved.get("main");
                JSONArray weather= (JSONArray) object_retrieved.get("weather");
                for(int i=0;i<weather.length();++i)
                {   JSONObject part=weather.getJSONObject(i);
                    weather_main=part.getString("main");
                    weather_description=part.getString("description");

                }
                JSONObject wind_details=(JSONObject) object_retrieved.get("wind");
                temp=main.getDouble("temp");
                wind_speed= (double) wind_details.get("speed");
            }
            catch(NullPointerException e){
                Log.i("Data delivered","NONE");
                Toast.makeText(getApplicationContext(),"Data could not be retireved from the server,check your spelling and try again",Toast.LENGTH_LONG).show();;
            } catch (JSONException e) {
                e.printStackTrace();

            }
            Intent i=new Intent(getApplicationContext(),SecondActivity.class);
            i.putExtra("main_weather",weather_main);
            i.putExtra("description",weather_description);
            i.putExtra("temp",temp);
            i.putExtra("wind_speed",wind_speed);
            startActivity(i);


        }
    }
}
