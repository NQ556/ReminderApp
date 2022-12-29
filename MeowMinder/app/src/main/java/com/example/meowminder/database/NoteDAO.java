package com.example.meowminder.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.meowminder.Note;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NoteDAO {
    @Insert
    void insertNote(Note note);

    @Query("SELECT * FROM note")
    List<Note> getNoteList();

    @Query("SELECT * FROM note WHERE id = :id")
    Note getNote(int id);

    @Query("DELETE FROM note")
    void delete();
}
