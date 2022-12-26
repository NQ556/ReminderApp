package com.example.meowminder;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    ExtendedFloatingActionButton addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Add new tasks
        addButton = (ExtendedFloatingActionButton) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAddNote();
            }
        });
        return view;
    }

    private void loadAddNote() {
        Intent intent = new Intent(getActivity(), AddNote.class);
        startActivity(intent);
    }
}