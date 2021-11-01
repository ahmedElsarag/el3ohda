package com.example.secretnotes;

import android.database.Observable;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.secretnotes.data.FavNotes;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface FavNotesDao {

    @Insert
    Completable insertNotes(FavNotes notes);

    @Query("select * from notes_table")
    Single<List<FavNotes>> getNotes();

    @Query("DELETE FROM notes_table WHERE noteID = :noteId")
     Completable deleteById(String noteId);
}
