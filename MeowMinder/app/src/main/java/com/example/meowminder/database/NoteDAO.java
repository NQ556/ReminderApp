package com.example.meowminder.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.meowminder.Note;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NoteDAO {
    @Insert
    long insertNote(Note note);

    @Query("SELECT * FROM note")
    List<Note> getNoteList();

    @Query("SELECT * FROM note WHERE id = :id")
    Note getNote(int id);

    @Query("SELECT * FROM note WHERE status = 1")
    List<Note> getNotDoneList();

    @Query("SELECT * FROM note WHERE status = 1 AND date = :date")
    List<Note> getNotDoneListByDate(String date);

    @Query("DELETE FROM note")
    void delete();

    @Update
    void updateNote(Note note);

    @Delete
    void deleteNote(Note note);
}
