package com.example.meowminder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.meowminder.database.NoteDatabase;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private List<Note> noteList;
    private List<Note> overdueNoteList;
    private View view;

    private LinearLayout noNotificationLayout;
    private RecyclerView notificationRcv;

    private NoteAdapter noteAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification, container, false);

        //Initialize UI
        initializeUI();

        //Load overdue note
        loadOverdueNote();

        //Set recycler view
        setUpRecyclerView();

        return view;
    }

    private void initializeUI() {
        noNotificationLayout = (LinearLayout) view.findViewById(R.id.no_notification_layout);
        notificationRcv = (RecyclerView) view.findViewById(R.id.notification_rcv);
    }

    private void loadOverdueNote() {
        noteList = new ArrayList<>();
        overdueNoteList = new ArrayList<>();

        noteList = NoteDatabase.getInstance(getActivity()).noteDAO().getNoteList();

        for (int i = 0; i < noteList.size(); i++)
        {
            if (noteList.get(i).getStatus() == Note.IS_OVERDUE)
            {
                overdueNoteList.add(noteList.get(i));
            }
        }
    }

    private void setUpRecyclerView() {
        //Set grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        notificationRcv.setLayoutManager(gridLayoutManager);

        //Set adapter
        noteAdapter = new NoteAdapter(overdueNoteList);
        notificationRcv.setAdapter(noteAdapter);

        //Check if there is any notes
        if (overdueNoteList != null)
        {
            noNotificationLayout.setVisibility(View.INVISIBLE);
            notificationRcv.setVisibility(View.VISIBLE);
        }
    }
}