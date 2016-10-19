package projects.boldurbogdan.newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by boldurbogdan on 26/06/2016.
 */
public class Database_helper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private final static String DATABASE_NAME = "sites.db";
    private final static String DATABASE_TABLE = "items";
    private final String COLUMN_NAME = "name";
    private final String COLUMN_url = "url";
    SQLiteDatabase db;


    public Database_helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table items(name text primary key not null" + ",url text not null);";
        db.execSQL(CREATE_TABLE);
        this.db = db;

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String delete_query = "drop table if exists " + DATABASE_TABLE;
        db.execSQL(delete_query);
        this.onCreate(db);
    }

    public void insert(Site_Item item_to_insert) {
        db = this.getWritableDatabase();
        ContentValues values_to_insert = new ContentValues();
        values_to_insert.put(COLUMN_NAME, item_to_insert.getName());
        values_to_insert.put(COLUMN_url, item_to_insert.getUrl());
        db.insert(DATABASE_TABLE, null, values_to_insert);
        Log.d("data inserted", item_to_insert.getUrl() + " " + item_to_insert.getName());

    }

    public ArrayList<Site_Item> retrieveAllRecords() {
        ArrayList<Site_Item> items = new ArrayList<>();
        db = this.getReadableDatabase();
        String sql_RetrieveAll = "select name,url from " + DATABASE_TABLE + ";";
        Cursor cursor = db.rawQuery(sql_RetrieveAll, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                Log.i("name,", name);
                String content = cursor.getString(1);
                Log.i("url",content);
                Site_Item item_to_add = new Site_Item(name, content);
                items.add(item_to_add);


            } while (cursor.moveToNext());
            cursor.close();
            return items;
        }
        return null;
    }

    public void delete_record(Site_Item name_of_the_item) {
        db=this.getWritableDatabase();
        db.delete(DATABASE_TABLE,"name=?",new String[]{name_of_the_item.getName()});
    }

    public void update_record(String item_to_update, String new_name) {
        db=this.getWritableDatabase();
        ContentValues values_to_update=new ContentValues();
        values_to_update.put("name",new_name);
        db.update(DATABASE_TABLE,values_to_update,"name=?",new String[]{item_to_update});
    }
}

