package com.example.secretnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
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
public class HomeFragment extends Fragment implements NotesAdapter.RecyclerViewClickListener, Filterable {

    FragmentHomeBinding binding;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseNotesReference;
    String uId;
    public static List<UserNote> allNotes;
    List<UserNote> exampleListFull;
    List<FavNotes> favoritNotes;
    List<String> notesKey;
    NotesAdapter notesAdapter;
    FavNotesDatabase database;
    List<UserNote> filteredList;

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

        binding.searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("xt","searched item = "+s);
                getFilter().filter(s);
                return false;
            }
        });
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
                exampleListFull = new ArrayList<>(allNotes);
                notesKey.add(key);
                notesAdapter.notifyDataSetChanged();
                notesAdapter.isShimmer = false;

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserNote userNote = dataSnapshot.getValue(UserNote.class);
                String key = dataSnapshot.getKey();
                int index=-1;
                for (int i=0;i<allNotes.size();i++){
                    String key2 = allNotes.get(i).getNoteID();
                    if (key.equals(key2))
                        index=i;
                }
//                int index = allNotes.indexOf(userNote);

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
        String title,desc,date,id,totalAmount,currentAmount;
        title = allNotes.get(position).getNoteTitle();
        desc = allNotes.get(position).getNoteDesc();
        date = allNotes.get(position).getNoteDate();
        id = allNotes.get(position).getNoteID();
        totalAmount = allNotes.get(position).getTotalAmount();
        currentAmount = allNotes.get(position).getCurrentAmount();
        boolean isLiked = allNotes.get(position).isLiked();
        String key = allNotes.get(position).getNoteID();


        if (isLiked){
            imageView.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            UserNote userNote = new UserNote(title,desc,date,key,totalAmount,currentAmount,false);
            updaeNote(userNote,key);
            database = FavNotesDatabase.getInstance(getActivity());
            database.favNotesDao().deleteById(allNotes.get(position).getNoteID())
                    .subscribeOn(Schedulers.computation())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                        }
                    });
        }else {
            imageView.setImageResource(R.drawable.ic_favorite_black_24dp);
            UserNote userNote = new UserNote(title,desc,date,key,totalAmount,currentAmount,true);
            updaeNote(userNote,key);

            database = FavNotesDatabase.getInstance(getActivity());
            database.favNotesDao().insertNotes(new FavNotes(title,desc,date,totalAmount,currentAmount,id))
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
                            Log.d("room", "onError:"+e.getMessage());
                        }
                    });
        }
    }

    // delete note when swipe left
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    final int position = viewHolder.getAdapterPosition();
                    final String deletednoteId = allNotes.get(position).getNoteID();
                    final UserNote userNote =
                            new UserNote(allNotes.get(position).getNoteTitle(), allNotes.get(position).getNoteDesc(), allNotes.get(position).getNoteDate(), allNotes.get(position).getNoteID(),allNotes.get(position).getTotalAmount(),allNotes.get(position).getCurrentAmount(),allNotes.get(position).isLiked());
                    Toast.makeText(getActivity(),"item = "+filteredList.get(position).getNoteTitle(),Toast.LENGTH_LONG).show();
                    allNotes.remove(allNotes.indexOf(filteredList.get(position)));
                    exampleListFull.remove(exampleListFull.indexOf(filteredList.get(position)));
                    notesKey.remove(notesKey.indexOf(filteredList.get(position).getNoteID()));
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

    public void updaeNote(UserNote updatedNote ,String key){
        databaseNotesReference.child(key).setValue(updatedNote).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Data updated successfully", Toast.LENGTH_LONG).show();
                    ((NavigationHost)getActivity()).navigateTo(new HomeFragment(),false);
                } else {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0 || constraint =="") {
                Log.d("xe","filtered list empty"+ exampleListFull.get(1).getNoteTitle());
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (UserNote item : exampleListFull) {
                    if (item.getNoteTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allNotes.clear();
            allNotes.addAll((List) results.values);
            notesAdapter.notifyDataSetChanged();
        }
    };
}
