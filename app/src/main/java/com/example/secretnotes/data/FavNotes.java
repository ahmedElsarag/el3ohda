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
    private String totalAmount;
    private String departedAmount;
    @PrimaryKey
    @NonNull
    private  String noteID;

    public FavNotes(String noteTitle, String noteDesc, String noteDate, String totalAmount, String departedAmount, @NonNull String noteID) {
        this.noteTitle = noteTitle;
        this.noteDesc = noteDesc;
        this.noteDate = noteDate;
        this.totalAmount = totalAmount;
        this.departedAmount = departedAmount;
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

    public String getTotalAmount() {
        return totalAmount;
    }


    public String getDepartedAmount() {
        return departedAmount;
    }

    @NonNull
    public String getNoteID() {
        return noteID;
    }
}
