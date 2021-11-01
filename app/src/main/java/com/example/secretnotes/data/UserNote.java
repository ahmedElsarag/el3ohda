package com.example.secretnotes.data;

import java.io.Serializable;

public class UserNote implements Serializable {

    String noteTitle="",noteDesc="", noteDate ="",noteID="",totalAmount="",currentAmount="";
    boolean isLiked=false;

    public UserNote() {
    }

    public UserNote(String noteTitle, String noteDesc, String noteData, String noteID,String totalAmount,String currentAmount,boolean isLiked) {
        this.noteTitle = noteTitle;
        this.noteDesc = noteDesc;
        this.noteDate = noteData;
        this.noteID = noteID;
        this.totalAmount = totalAmount;
        this.currentAmount = currentAmount;
        this.isLiked = isLiked;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteDesc() {
        return noteDesc;
    }

    public void setNoteDesc(String noteDesc) {
        this.noteDesc = noteDesc;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNoteID() {
        return noteID;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(String currentAmount) {
        this.currentAmount = currentAmount;
    }
}
