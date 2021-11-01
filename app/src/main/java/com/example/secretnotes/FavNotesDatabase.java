package com.example.secretnotes;

import android.content.Context;
import android.content.Entity;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.secretnotes.data.FavNotes;

@Database(entities = FavNotes.class,version = 3,exportSchema = false)
public abstract class FavNotesDatabase extends RoomDatabase {

    private static FavNotesDatabase instance;
    public abstract FavNotesDao favNotesDao();

    public static synchronized FavNotesDatabase getInstance(Context context){

        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),FavNotesDatabase.class,"fav_notes")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
