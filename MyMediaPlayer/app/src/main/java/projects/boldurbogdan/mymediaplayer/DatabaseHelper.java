package projects.boldurbogdan.mymediaplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by boldurbogdan on 16/07/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    Context context;
    private final static String DatabaseName = "playlists.db";
    private final static String DatabaseTablePlaylist = "playlist";
    private final static String DatabaseTableSong = "song";
    private final static String DatabaseTableSong_PLaylist = "songplaylist";
    private final static int DATABASE_VERSION = 12;
    private SQLiteDatabase db;


    public DatabaseHelper(Context c) {
        super(c, DatabaseName, null, DATABASE_VERSION);

    }

    public void onCreate(SQLiteDatabase db) {
        String create = "create table if not exists playlist(name TEXT primary key not null," +
                         "notrack INTEGER not null);";

        String create2 = "create table if not exists song(" +
                "id INTEGER primary key not null," +
                "name TEXT not null," +
                "duration Integer not null," +
                "path TEXT not null,art TEXT not null," +
                "artist TEXT not null);";

        String create3 = "create table if not exists songplaylist(" +
                "id INTEGER primary key autoincrement," +
                "id_song INTEGER REFERENCES song(id)," +
                "name_pl TEXT REFERENCES playlist(name));";
        db.execSQL(create);
        db.execSQL(create2);
        db.execSQL(create3);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String deletetablesong = "drop table if exists " + DatabaseTableSong + ";";
        String deletetableplaylist = "drop table if exists " + DatabaseTablePlaylist + ";";
        String deletetablesongplaylist = "drop table if exists " + DatabaseTableSong_PLaylist + ";";
        db.execSQL(deletetablesongplaylist);
        db.execSQL(deletetablesong);
        db.execSQL(deletetableplaylist);
        this.onCreate(db);
    }
    public void updateNumberOfTracks(String playlistName,int newNumberOfTracks){
        db=this.getWritableDatabase();
        ContentValues valueToInsert=new ContentValues();
        valueToInsert.put("notrack",newNumberOfTracks);
        db.update(DatabaseTablePlaylist,valueToInsert,"name=?",new String[]{playlistName});
    }
    public void insertPlaylist(Playlist playlist) {
        db = this.getWritableDatabase();
        ContentValues valuetoinsert = new ContentValues();
        valuetoinsert.put("name", playlist.getName());
        valuetoinsert.put("notrack", playlist.getNumber_of_tracks());
        db.insert(DatabaseTablePlaylist, null, valuetoinsert);
    }

    public void insertSong(Song song) {
        db = this.getWritableDatabase();
        ContentValues valuesToInsert = new ContentValues();
        valuesToInsert.put("id", song.getId());
        valuesToInsert.put("name", song.getName());
        valuesToInsert.put("duration", song.getDuration());
        valuesToInsert.put("path", song.getPath());
        valuesToInsert.put("art", String.valueOf(song.getArt()));
        valuesToInsert.put("artist", song.getArtist());
        db.insert("song", null, valuesToInsert);
    }

    public ArrayList<String> retrivesongnames() {
        db = this.getReadableDatabase();
        ArrayList<String> names = new ArrayList<>();
        String select = "select * from (select  from " + DatabaseTableSong + ";";
        Cursor cursor = db.rawQuery(select, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                names.add(name);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return names;
    }

    public ArrayList<Playlist> getPlaylists() {
        db = this.getReadableDatabase();
        ArrayList<Playlist> playlists = new ArrayList<>();
        String sql = "select * from " + DatabaseTablePlaylist + ';';
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Integer notracks = cursor.getInt(cursor.getColumnIndex("notrack"));
                Playlist newPlaylist = new Playlist(name, notracks);
                playlists.add(newPlaylist);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return playlists;
    }

    public ArrayList<Song> retrieveSongsForSpecificPlaylist(String nameOfPlaylist) {
        ArrayList<Song> songsForPlaylist = new ArrayList<>();
        if(nameOfPlaylist.matches("")){
            Log.i("error,no name found",nameOfPlaylist);
        }
        else {
            db = this.getReadableDatabase();

            String sql = "select * from song where id in(select id_song from " + DatabaseTableSong_PLaylist + " where name_pl="+"'"+nameOfPlaylist+"'"+");";
            Cursor cursor = db.rawQuery(sql,null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Song newsong = new Song(cursor.getLong(cursor.getColumnIndex("id")), cursor.getString(cursor.getColumnIndex("name")),
                            Uri.parse(cursor.getString(cursor.getColumnIndex("art"))),
                            cursor.getString(cursor.getColumnIndex("duration")), cursor.getString(cursor.getColumnIndex("artist"))
                            , cursor.getString(cursor.getColumnIndex("path")));
                    songsForPlaylist.add(newsong);
                } while (cursor.moveToNext());
            }
            cursor.close();

        }
        return songsForPlaylist;
    }

    public ArrayList<Playlist> getplaylists(){
        db=this.getReadableDatabase();
        ArrayList<Playlist> playlists=new ArrayList<>();
        String sql="select * from "+DatabaseTablePlaylist+";";
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor!=null && cursor.moveToFirst()){
            do{
                Playlist newpl=new Playlist(cursor.getString(cursor.getColumnIndex("name")),
                                            cursor.getInt(cursor.getColumnIndex("notrack")));
                playlists.add(newpl);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return playlists;
    }
    public int songAlreadyInPlaylist(Long ID,String nameOfPlaylist){
        db=this.getReadableDatabase();
        String sqlQuery="select * from "+DatabaseTableSong_PLaylist+" where id_song="+ID+" and name_pl="+"'"+nameOfPlaylist+"'"+";";
        Cursor cursor=db.rawQuery(sqlQuery,null);
        if(cursor!=null && cursor.moveToFirst()){
            cursor.close();
            return 1;
        }
        else{
            cursor.close();
            return 0;
        }
    }
    public int getNumberOfTracksForPlaylist(String namePlaylist){
        db=this.getReadableDatabase();
        String sqlQuery="select notrack from playlist where name="+"'"+namePlaylist+"'"+";";
        Cursor cursor=db.rawQuery(sqlQuery,null);
        if(cursor!=null){
            cursor.moveToFirst();
            int numberOfTracks=cursor.getInt(cursor.getColumnIndex("notrack"));
            cursor.close();
            return numberOfTracks;


        }
        return -1;
    }
    public void insertSongInPlyalist(long song_id,String playlistname){
        db=this.getWritableDatabase();
            ContentValues valuesToInsert = new ContentValues();
            valuesToInsert.put("id_song", song_id);
            valuesToInsert.put("name_pl", playlistname);
            db.insert(DatabaseTableSong_PLaylist, null, valuesToInsert);

    }
    public void removePlaylist(String namePlaylist){
        db=getWritableDatabase();
        db.delete(DatabaseTablePlaylist,"name=?",new String[]{namePlaylist});
    }
    public void updatePlaylist(String namePlaylist,String newName){
        db=getWritableDatabase();
        ContentValues valueToUpdate=new ContentValues();
        ContentValues valueToUpdate2=new ContentValues();
        valueToUpdate.put("name",newName);
        valueToUpdate2.put("name_pl",newName);
        try {
            db.update(DatabaseTableSong_PLaylist, valueToUpdate2, "name_pl=?", new String[]{namePlaylist});
            db.update(DatabaseTablePlaylist, valueToUpdate, "name=?", new String[]{namePlaylist});
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    public void deleteSongFromPlaylist(Long songId,String playlistName){
        db=getWritableDatabase();
        ContentValues valuesToDelete=new ContentValues();
        valuesToDelete.put("id_song",songId);
        valuesToDelete.put("name_pl",playlistName);
        db.delete(DatabaseTableSong_PLaylist,"id_song=? and name_pl="+"'"+playlistName+"'",new String[]{String.valueOf(songId)});
    }
}
