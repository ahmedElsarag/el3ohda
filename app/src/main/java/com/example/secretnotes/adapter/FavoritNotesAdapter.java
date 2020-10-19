package com.example.secretnotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secretnotes.R;
import com.example.secretnotes.data.FavNotes;
import com.example.secretnotes.data.UserNote;
import com.example.secretnotes.databinding.CardNoteBinding;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class FavoritNotesAdapter extends RecyclerView.Adapter<FavoritNotesAdapter.FavoritNotesHolder>{

    Context context;
    List<FavNotes> list = new ArrayList<>();
    public boolean isShimmer = false;
    int shimmerNum = 5;


    public FavoritNotesAdapter (Context context) {
        this.context = context;
    }
    public void setNotes(List<FavNotes> list){
        this.list = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FavoritNotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        CardNoteBinding binding = DataBindingUtil.inflate(layoutInflater,R.layout.card_note,parent,false);
        return new FavoritNotesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritNotesHolder holder, int position) {

        if (isShimmer){
            holder.binding.shimmerLayout.startShimmer();
        }else {
            holder.binding.shimmerLayout.stopShimmer();
            holder.binding.shimmerLayout.setShimmer(null);
            holder.binding.title.setText(list.get(position).getNoteTitle());
            holder.binding.title.setBackground(null);
            holder.binding.desc.setText(list.get(position).getNoteDesc());
            holder.binding.desc.setBackground(null);
            holder.binding.date.setText(list.get(position).getNoteDate());
            holder.binding.date.setBackground(null);
        }

    }

    @Override
    public int getItemCount() {
        return isShimmer ? shimmerNum : list.size();
    }

    public class FavoritNotesHolder extends RecyclerView.ViewHolder{
        CardNoteBinding binding;
        public FavoritNotesHolder(@NonNull CardNoteBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
