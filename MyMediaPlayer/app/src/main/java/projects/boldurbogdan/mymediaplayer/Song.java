package projects.boldurbogdan.mymediaplayer;

import android.graphics.Bitmap;
import android.net.Uri;
import android.renderscript.Float2;
import android.renderscript.Float3;
import android.util.Log;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Created by boldurbogdan on 15/07/2016.
 */
public class Song {
    private String name;
    private Long id;
    private Uri art;
    private String duration;
    private String artist;
    private String path;
    public int real_duration_in_minutes;
    private boolean isSelected;

    public Song(Long id, String name, Uri image, String duration, String artist,String path){
        float duration_in_minutes_prior_transformation;
        this.name=name;
        this.id=id;
        this.art=image;
        this.duration=duration;
        this.artist=artist;
        this.path=path;
        try {
            duration_in_minutes_prior_transformation = (Float.parseFloat(duration) / 60000) * 100;
            Log.i("duration", String.valueOf(duration_in_minutes_prior_transformation));
            real_duration_in_minutes = (int) duration_in_minutes_prior_transformation;
            int minutes = real_duration_in_minutes / 100;
            int seconds = real_duration_in_minutes % 100;
            this.duration = String.valueOf(minutes) + ":" + String.valueOf(seconds);
        }
        catch (Exception e){
            this.duration=duration;
        }
        this.isSelected=false;


    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Uri getArt() {
        return art;
    }

    public void setArt(Uri art) {
        this.art = art;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
