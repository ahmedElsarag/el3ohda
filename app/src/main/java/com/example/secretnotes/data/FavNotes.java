package com.example.secretnotes.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.annotations.NotNull;

@Entity(tableName = "notes_table")
public class FavNotes {
    private String noteTitle;
    private String noteDesc;
    private String noteDate;
    @PrimaryKey
    @NonNull
    private  String noteID;

    public FavNotes(String noteTitle, String noteDesc, String noteDate, String noteID) {
        this.noteTitle = noteTitle;
        this.noteDesc = noteDesc;
        this.noteDate = noteDate;
        this.noteID = noteID;
    }

    public String getNoteTitle() {
        return noteTitle;
    }


    public String getNoteDesc() {
        return noteDesc;
    }

    public String getNoteDate() {
        return noteDate;
    }


    public String getNoteID() {
        return noteID;
    }

}
