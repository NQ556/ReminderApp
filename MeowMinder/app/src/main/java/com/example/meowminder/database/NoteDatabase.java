package com.example.meowminder.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.meowminder.Converters;
import com.example.meowminder.Note;

@Database(entities = {Note.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class NoteDatabase extends RoomDatabase {

    static Migration migration_from_1_to_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE note ADD COLUMN ringtone TEXT");
            database.execSQL("CREATE TABLE note_tmp(id INTEGER PRIMARY KEY NOT NULL, title TEXT, date TEXT, time TEXT, taskList TEXT, isAlarmOn INTEGER NOT NULL, ringtone TEXT, status INTEGER NOT NULL)");
            database.execSQL("INSERT INTO note_tmp SELECT id, title, date, time, taskList, isAlarmOn, ringtone, status FROM note");
            database.execSQL("DROP TABLE note");
            database.execSQL("ALTER TABLE note_tmp RENAME TO note");
        }
    };

    private static final String DATABASE_NAME = "note.db";
    private static NoteDatabase instance;

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(migration_from_1_to_2)
                    .build();
        }
        return instance;
    }

    public abstract NoteDAO noteDAO();
}
