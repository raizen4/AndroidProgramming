package projects.boldurbogdan.myfirstapp;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void click_Convert(View view) {
        EditText currency_from_field = (EditText) findViewById(R.id.Currency_to_convert_from);
        String currency_F_text = currency_from_field.getText().toString();
        EditText currency_to_field = (EditText) findViewById(R.id.currency_to_convert_into);
        String currency_T_text = currency_to_field.getText().toString();
        Log.i("foreign currency", currency_F_text);
        Log.i("transform in", currency_T_text);
        if (currency_F_text.matches("") || currency_T_text.matches("")) {
            Toast.makeText(getApplicationContext(), "One or both of the fields are invalid,please fill them " +
                    "in before pressing Convert", Toast.LENGTH_LONG).show();

        } else {
            // http://api.fixer.io/latest?base=USD;symbols=GBP,EUR,RON
            DownloadTask site = new DownloadTask();
            site.execute("http://api.fixer.io/latest?base=" + currency_F_text + ";" + "symbols=" + currency_T_text);


        }

    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... passed_url) {
            String result = "";
            URL url_to_connect;
            HttpURLConnection connect = null;
            try {
                url_to_connect = new URL(passed_url[0]);
                connect = (HttpURLConnection) url_to_connect.openConnection();
                InputStream in = connect.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
            Log.i("data:",result);
            return result;

            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            return null;

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("DATA delivered", result);
            EditText currency_to_field = (EditText) findViewById(R.id.currency_to_convert_into);
            String currency_T_text = currency_to_field.getText().toString();
            try{
                JSONObject jobj=new JSONObject(result);
                 jobj=jobj.getJSONObject("rates");
                 double rate=jobj.getDouble(currency_T_text);
                 Log.i("value is", String.valueOf(rate));
                 EditText final_result_field=(EditText)findViewById(R.id.final_result_field);
                 final_result_field.setText(String.valueOf(rate),TextView.BufferType.EDITABLE);




                }
            catch (JSONException e1) {
                e1.printStackTrace();
            }
              catch(Exception e){
                e.printStackTrace();

            }

        }
    }
}