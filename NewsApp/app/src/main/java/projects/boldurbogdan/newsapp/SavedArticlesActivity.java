package projects.boldurbogdan.newsapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class SavedArticlesActivity extends AppCompatActivity {
    SQLiteDatabase db;
    ListView listView;
    ArrayList<Site_Item>pointer_items=new ArrayList<>();
    BidiMap<Site_Item,String>map_items_names=new DualHashBidiMap<>();
    String current_item_long_pressed;
    ArrayList<String> strings_names;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_articles_activity);
        Database_helper dbManager = new Database_helper(getApplicationContext());
        pointer_items = dbManager.retrieveAllRecords();
        for (Site_Item item : pointer_items) {
            map_items_names.put(item, item.getName());
        }
        strings_names=new ArrayList<>(map_items_names.values());
        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simple_layout_for_database,R.id.textview_database,strings_names );
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                current_item_long_pressed=strings_names.get(position);
                try{
                    Toast.makeText(getApplicationContext(), String.format("You have long pressed %s", map_items_names.getKey(strings_names.get(position)).getName()),Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    Log.e("error at item",String.valueOf(map_items_names.getKey(strings_names.get(position))));
                }

            return false;};
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(getApplicationContext(),WebActivity.class);
                i.putExtra("url",map_items_names.getKey(strings_names.get(position)).getUrl());
                startActivity(i);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listView){
            MenuInflater menuinflater=getMenuInflater();
            menuinflater.inflate(R.menu.contextual_meu_item_database,menu);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.Edit_button:
                LayoutInflater inflater=getLayoutInflater();
                final View prompt=inflater.inflate(R.layout.prompt_edit_articles,null);

                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(SavedArticlesActivity.this);
                alertDialogBuilder.setCancelable(false).setTitle("Edit name").setPositiveButton("Ok",null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setView(prompt);
                         final AlertDialog dialog=alertDialogBuilder.create();

                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog_interface) {
                                Button butt_ok=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                butt_ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        EditText text_field= (EditText) prompt.findViewById(R.id.txt_for_editing_article);
                                        String text_entered=text_field.getText().toString();
                                        Log.i("new name",text_entered);
                                        if (text_entered.matches("")){
                                            Toast.makeText(getApplicationContext(),"You need to enter a name before submitting",Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            Log.i("item pressed",current_item_long_pressed);
                                            map_items_names.getKey(current_item_long_pressed).setName(text_entered);
                                            Log.i("new item name",map_items_names.getKey(current_item_long_pressed).getName());
                                            for (int i=0;i<strings_names.size();++i){
                                                if (strings_names.get(i).matches(current_item_long_pressed)){
                                                    strings_names.set(i,text_entered);
                                                    ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
                                                    break;
                                                }
                                            }

                                            Database_helper helper=new Database_helper(getApplicationContext());
                                            helper.update_record(current_item_long_pressed,text_entered);
                                            dialog.dismiss();

                                        }
                                    }
                                });
                            }
                        });
                         dialog.show();
                         break;

            case R.id.Delete_button:
                LayoutInflater inflater2=getLayoutInflater();
                View prompt_delete=inflater2.inflate(R.layout.prompt_delete,null);
                AlertDialog.Builder builder2=new AlertDialog.Builder(SavedArticlesActivity.this);
                builder2.setView(prompt_delete);
                builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i=0;i<strings_names.size();++i){
                            if (strings_names.get(i).matches(current_item_long_pressed)){
                                Database_helper helper=new Database_helper(getApplicationContext());
                                helper.delete_record(map_items_names.getKey(strings_names.get(i)));
                                map_items_names.remove(map_items_names.getKey(strings_names.get(i)));
                                strings_names.remove(i);
                                ((ArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
                                break;
                            }
                        }

                    }
                }).setCancelable(false);
                final AlertDialog dialog2=builder2.create();
                dialog2.show();
                break;

                default:
                    return super.onContextItemSelected(item);

        }


    return true;
    }
}
