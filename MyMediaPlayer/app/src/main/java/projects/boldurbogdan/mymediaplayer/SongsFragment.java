package projects.boldurbogdan.mymediaplayer;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {
    SearchView searchView;
    ListView listView;
    protected boolean pressedAtLeastOneSongInThePlaylistInSongsFragment = false;
    listener mCallback;
    Activity mActivity;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mCallback = (listener) mActivity;

    }


    interface listener {
         void bringBackbool(boolean boolSentBack);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        searchView = (SearchView) getView().findViewById(R.id.sort_songs_for_adding_to_playlist);
        listView = (ListView) getView().findViewById(R.id.listView);
        searchView.setQueryHint("search song...");


        final SongAdapter adapter = new SongAdapter(getContext(), ((MainActivity) getActivity()).mysongs);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!pressedAtLeastOneSongInThePlaylistInSongsFragment) {
                    pressedAtLeastOneSongInThePlaylistInSongsFragment = true;
                    mCallback.bringBackbool(pressedAtLeastOneSongInThePlaylistInSongsFragment);
                    pressedAtLeastOneSongInThePlaylistInSongsFragment=false;
                }

                if (((MainActivity) getActivity()).myservice.mp == null) {
                    ((MainActivity) getActivity()).currentPosPressedInMainActivity = position;
                    ((MainActivity) getActivity()).myservice.playSong(position);
                    Intent i=new Intent(getActivity(),CurrentlyPlayingActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(i);

                } else {
                    if (position != ((MainActivity) getActivity()).currentPosPressedInMainActivity) {
                        ((MainActivity) getActivity()).currentPosPressedInMainActivity = position;
                        ((MainActivity) getActivity()).myservice.playSong(((MainActivity) getActivity()).currentPosPressedInMainActivity);
                        Intent i = new Intent(getActivity(), CurrentlyPlayingActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                        startActivity(i);
                    }

                }
            }

        });
        listView.setTextFilterEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((SongAdapter) listView.getAdapter()).getFilter().filter(newText);
                return true;
            }


        });

    }
}




