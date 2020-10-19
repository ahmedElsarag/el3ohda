package com.example.secretnotes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.secretnotes.adapter.FavoritNotesAdapter;
import com.example.secretnotes.adapter.NotesAdapter;
import com.example.secretnotes.data.FavNotes;
import com.example.secretnotes.data.UserNote;
import com.example.secretnotes.databinding.FragmentFavouritBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritFragment extends Fragment {

    FragmentFavouritBinding binding;
    public List<UserNote> favNotes;
    FavoritNotesAdapter notesAdapter;
    FavNotesDatabase database;

    public FavouritFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavouritBinding.inflate(getLayoutInflater());

//        intiateRecyclerView();
        intiateRecyclerView();
        getNotes();

        return binding.getRoot();
    }

    private void getNotes() {
        database = FavNotesDatabase.getInstance(getActivity());
        database.favNotesDao().getNotes()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<FavNotes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<FavNotes> favNotes) {
                        notesAdapter.setNotes(favNotes);
                        notesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void intiateRecyclerView() {
        favNotes = new ArrayList<>();
        favNotes.add(new UserNote("ahmed", "hjhjhh jhhhjh jhh", "25/10", "ghghghg"));
        binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycler.setHasFixedSize(true);
        notesAdapter = new FavoritNotesAdapter(getContext());
        binding.recycler.setAdapter(notesAdapter);
    }
}
