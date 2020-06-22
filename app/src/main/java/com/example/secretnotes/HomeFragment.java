package com.example.secretnotes;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.secretnotes.adapter.NotesAdapter;
import com.example.secretnotes.data.UserNote;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements NotesAdapter.RecyclerViewClickListener {

    SharedPreferences sharedPreferences;
    DatabaseReference databaseNotesReference;
    String uId;
    public static List<UserNote> allNotes;
    List<String> notesKey;
    NotesAdapter notesAdapter;
    RecyclerView recyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler);

        sharedPreferences = getActivity().getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);
        uId = sharedPreferences.getString("UID", "");

        databaseNotesReference = FirebaseDatabase.getInstance().getReference("USERNOTES").child(uId);

        intiateRecyclerView();

        readNotes();

        return view;
    }

    public void intiateRecyclerView() {
        allNotes = new ArrayList<>();
        notesKey = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        notesAdapter = new NotesAdapter(getContext(), allNotes, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(notesAdapter);
    }

    public void readNotes() {
        databaseNotesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserNote userNote = dataSnapshot.getValue(UserNote.class);
                String key = dataSnapshot.getKey();
                allNotes.add(userNote);
                notesKey.add(key);
                notesAdapter.notifyDataSetChanged();
                notesAdapter.isShimmer = false;

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserNote userNote = dataSnapshot.getValue(UserNote.class);
                String key = dataSnapshot.getKey();
                int index = notesKey.indexOf(key);

                allNotes.set(index, userNote);
                notesAdapter.notifyDataSetChanged();
                notesAdapter.isShimmer = false;
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void editClick(int position) {
        ((NavigationHost) (getActivity())).navigateTo(new AddNotesFragment(), true);
        Variables.isAdd = false;
        Variables.pos = position;
    }

    // delete note when swipe left
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition();
                    final String deletednoteId = allNotes.get(position).getNoteID();
                    final UserNote userNote =
                            new UserNote(allNotes.get(position).getNoteTitle(), allNotes.get(position).getNoteDesc(), allNotes.get(position).getNoteDate(), allNotes.get(position).getNoteID());
                    allNotes.remove(position);
                    notesAdapter.notifyDataSetChanged();
                    confirmationDialog(position,userNote,deletednoteId);

                }
            };

    public void confirmationDialog(final int position,final UserNote userNote,final String deletedNoteId) {
        SweetAlertDialog.DARK_STYLE = true;
        // 2. Confirmation message
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("You won't be able to recover this Notes!")
                .setConfirmText("Delete!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        databaseNotesReference.child(deletedNoteId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(getContext(), "done", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                            }
                        });
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        allNotes.add(position,userNote);
                        notesAdapter.notifyDataSetChanged();
                    }
                })
                .show();
    }
}
