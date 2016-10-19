package projects.boldurbogdan.mymediaplayer;

/**
 * Created by boldurbogdan on 20/09/2016.
 */
public class YoutubeObject {

    private String name;
    private String photo;
    private String videoId;

    public YoutubeObject(String nameOfObject,String photoOfSong,String videoID){
        this.name=nameOfObject;
        this.photo=photoOfSong;
        this.videoId=videoID;
    }

    public String getName() {
        return name;
    }
    public String getVideoId(){return videoId;}

    public String getPhoto() {
        return photo;
    }
}
