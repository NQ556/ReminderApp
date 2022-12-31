package com.example.meowminder;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.meowminder.database.NoteDatabase;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private View view;

    private ExtendedFloatingActionButton addButton;
    private RecyclerView noteRcv;

    private LinearLayout noNoteLayout;

    private NoteAdapter noteAdapter;
    private List<Note> noteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        //Initialize UI
        initializeUI();
        
        //Set up recycler view
        setUpRecyclerView();

        //Show "Empty background" if list note is empty
        showNotes();

        //Add new tasks by click add button
        clickAddButton();

        return view;
    }

    private void initializeUI() {
        addButton = (ExtendedFloatingActionButton) view.findViewById(R.id.add_button);
        noteRcv = (RecyclerView) view.findViewById(R.id.note_rcv);
        noNoteLayout = (LinearLayout) view.findViewById(R.id.no_note_layout);
    }

    private void setUpRecyclerView() {
        //Set grid layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        noteRcv.setLayoutManager(gridLayoutManager);

        //Get list of notes from database
        noteList = new ArrayList<>();
        noteList = NoteDatabase.getInstance(getActivity()).noteDAO().getNoteList();

        //Set adapter
        noteAdapter = new NoteAdapter(noteList);
        noteRcv.setAdapter(noteAdapter);
    }

    private void showNotes() {
        if (noteList.size() > 0)
        {
            noteRcv.setVisibility(View.VISIBLE);
            noNoteLayout.setVisibility(View.INVISIBLE);
        }

        else
        {
            noNoteLayout.setVisibility(View.VISIBLE);
            noteRcv.setVisibility(View.INVISIBLE);
        }
    }

    private void clickAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAddNote();
            }
        });
    }

    private void loadAddNote() {
        Intent intent = new Intent(getActivity(), AddNote.class);
        startActivity(intent);;
    }
}