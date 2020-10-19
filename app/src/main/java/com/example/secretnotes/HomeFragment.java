package com.example.secretnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.secretnotes.adapter.NotesAdapter;
import com.example.secretnotes.data.FavNotes;
import com.example.secretnotes.data.UserNote;
import com.example.secretnotes.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements NotesAdapter.RecyclerViewClickListener {

    FragmentHomeBinding binding;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseNotesReference;
    String uId;
    public static List<UserNote> allNotes;
    List<FavNotes> favoritNotes;
    List<String> notesKey;
    NotesAdapter notesAdapter;
    FavNotesDatabase database;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        sharedPreferences = getActivity().getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);
        uId = sharedPreferences.getString("UID", "");
        Variables.uId = uId;

        databaseNotesReference = FirebaseDatabase.getInstance().getReference("USERNOTES").child(uId);

        intiateRecyclerView();

        readNotes();

        return binding.getRoot();
    }

    public void intiateRecyclerView() {
        allNotes = new ArrayList<>();
        favoritNotes =new ArrayList<>();
        notesKey = new ArrayList<>();
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.setHasFixedSize(true);
        notesAdapter = new NotesAdapter(getContext(), allNotes, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recycler);
        binding.recycler.setAdapter(notesAdapter);
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

    @Override
    public void addToFavorit(int position, ImageView imageView) {
        String title,desc,date,id;
        title = allNotes.get(position).getNoteTitle();
        desc = allNotes.get(position).getNoteDesc();
        date = allNotes.get(position).getNoteDate();
        id = allNotes.get(position).getNoteID();

        imageView.setImageResource(R.drawable.ic_favorite_black_24dp);

        database = FavNotesDatabase.getInstance(getActivity());
        database.favNotesDao().insertNotes(new FavNotes(title,desc,date,id))
                .subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

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
                    notesKey.remove(position);
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
