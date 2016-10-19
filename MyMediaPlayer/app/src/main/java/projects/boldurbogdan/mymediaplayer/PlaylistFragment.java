package projects.boldurbogdan.mymediaplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {
    ListView ListView;
    ArrayList<Playlist>myplaylists=new ArrayList<>();
    String[]curr_posItem_pressed=new String[2];
    public PlaylistFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DatabaseHelper helper=new DatabaseHelper(getActivity().getApplicationContext());
        final LayoutInflater inflater=getActivity().getLayoutInflater();
        final View footerview;
        ListView= (ListView) getView().findViewById(R.id.playlist_listview);
        registerForContextMenu(ListView);
        footerview = inflater.inflate(R.layout.footer_view_for_listview,null);
        ImageButton addplaylist=(ImageButton)footerview.findViewById(R.id.footerButton);
        myplaylists=helper.getplaylists();
        PlaylistAdapter adapter=new PlaylistAdapter(getActivity().getApplicationContext(),myplaylists);
        try {


            ListView.setAdapter(adapter);
            ListView.addFooterView(footerview);
        }
        catch (Exception e){
            e.toString();
        }


        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer code=1;
                curr_posItem_pressed[0]=myplaylists.get(position).getName();
                curr_posItem_pressed[1]=String.valueOf(position);
                Intent intent=new Intent(getActivity(),PlaylistSongs.class);
                intent.putExtra("nameOfPlaylist",curr_posItem_pressed[0]);
                intent.putExtra("position",curr_posItem_pressed[1]);
                intent.putExtra("code",code);
                freeMemory();
                startActivity(intent);

            }
        });
        ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               curr_posItem_pressed[0]=myplaylists.get(position).getName();
                curr_posItem_pressed[1]=String.valueOf(position);
                Toast.makeText(getActivity().getApplicationContext(),curr_posItem_pressed[0]+" "+curr_posItem_pressed[1],Toast.LENGTH_LONG).show();
                return false;
            }
        });
        addplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setView(R.layout.prompt_for_adding_playlist).setPositiveButton("Create",null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setTitle("Create playlist");
               final AlertDialog createPromptDialog=builder.create();
                createPromptDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button buttoncreate=createPromptDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        buttoncreate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText textfield= (EditText) createPromptDialog.findViewById(R.id.usertextfield);
                                final String textentered=textfield.getText().toString();
                                if(textentered.matches("")){
                                    Toast.makeText(getActivity().getApplicationContext(),"You must enter a name for the playlist",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Playlist newplaylist=new Playlist(textentered,0);
                                    DatabaseHelper helper=new DatabaseHelper(getActivity().getApplicationContext());
                                    helper.insertPlaylist(newplaylist);
                                    myplaylists.add(newplaylist);
                                    ((PlaylistAdapter)((HeaderViewListAdapter)ListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                                    createPromptDialog.dismiss();




                                }
                            }
                        });
                    }
                });
                createPromptDialog.show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu,v,menuInfo);
            if(v.getId()==R.id.playlist_listview){
            MenuInflater inflater=getActivity().getMenuInflater();
            inflater.inflate(R.menu.contextual_menu_playlists,menu);
            }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Add_songs_playlist:
                Integer code=2;
                Intent intent=new Intent(getActivity(),SelectSongsForPLaylist.class);
                intent.putExtra("name_of_playlist",curr_posItem_pressed[0]);
                intent.putExtra("code",code);
                freeMemory();
                startActivity(intent);
                break;
            case R.id.Delete_playlist:
                DatabaseHelper helper=new DatabaseHelper(getActivity().getApplicationContext());
                helper.removePlaylist(curr_posItem_pressed[0]);
                myplaylists.remove(Integer.parseInt(curr_posItem_pressed[1]));
                ((PlaylistAdapter)((HeaderViewListAdapter)ListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                break;


            case R.id.Edit_name_playlist:
                AlertDialog.Builder editnamebuilder=new AlertDialog.Builder(getActivity());
                editnamebuilder.setTitle("Change title").setPositiveButton("OK",null).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setView(R.layout.edit_name_of_playlist);
                final AlertDialog editNamePlaylistDialog=editnamebuilder.create();
                editNamePlaylistDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button butt_ok=editNamePlaylistDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        butt_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText field= (EditText) editNamePlaylistDialog.findViewById(R.id.editPlaylistNameField);
                                String textEntered=field.getText().toString();
                                if(textEntered.matches("")){
                                    Toast.makeText(getActivity().getApplicationContext(),"You must enter a name",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    DatabaseHelper databaseHelper=new DatabaseHelper(getActivity().getApplicationContext());
                                    databaseHelper.updatePlaylist(curr_posItem_pressed[0],textEntered);
                                    myplaylists.get(Integer.parseInt(curr_posItem_pressed[1])).setName(textEntered);
                                    ((PlaylistAdapter)((HeaderViewListAdapter)ListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                                    editNamePlaylistDialog.dismiss();
                                }
                            }
                        });

                    }
                });
                editNamePlaylistDialog.show();
                break;
            default:
            super.onContextItemSelected(item);
        }

    return true;}

    public void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

}