package projects.boldurbogdan.mymediaplayer;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import static android.R.attr.id;

public class WebActivityForYoutube extends AppCompatActivity {
 WebView site;
    String url;
    ImageButton buttonForDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        setContentView(R.layout.activity_web_for_youtube);
        site = (WebView) findViewById(R.id.webViewForYoutube);
        buttonForDownload= (ImageButton) findViewById(R.id.downloadYoutube);
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
}
