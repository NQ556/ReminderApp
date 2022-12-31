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

@Database(entities = {Note.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class NoteDatabase extends RoomDatabase {

    static Migration migration = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE note ADD COLUMN intentCode INTEGER NOT NULL DEFAULT '0'");
        }
    };

    private static final String DATABASE_NAME = "note.db";
    private static NoteDatabase instance;

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .addMigrations(migration)
                    .build();
        }
        return instance;
    }

    public abstract NoteDAO noteDAO();
}
