package projects.boldurbogdan.mymediaplayer;

/**
 * Created by boldurbogdan on 16/07/2016.
 */
public class Playlist {
    private String name;
    private int number_of_tracks;
  public Playlist(String name,int number_of_tracks){
      this.name=name;
      this.number_of_tracks=number_of_tracks;
  }


    public int getNumber_of_tracks() {
        return number_of_tracks;
    }

    public void setNumber_of_tracks(int number_of_tracks) {
        this.number_of_tracks = number_of_tracks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
