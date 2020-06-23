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
import com.example.secretnotes.data.UserNote;
import com.example.secretnotes.databinding.CardNoteBinding;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder> {
    Context context;
    List<UserNote> list = new ArrayList<>();
    private RecyclerViewClickListener listener;
   public boolean isShimmer = true;
    int shimmerNum = 5;


    public NotesAdapter(Context context, List<UserNote> list, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        CardNoteBinding binding = DataBindingUtil.inflate(inflater,R.layout.card_note,parent,false);
        return new NotesHolder(binding,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {

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
        return isShimmer?shimmerNum:list.size();
    }

    public class NotesHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardNoteBinding binding;
        private RecyclerViewClickListener mListener;

        public NotesHolder(@NonNull CardNoteBinding binding ,RecyclerViewClickListener mListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.mListener = mListener;
            binding.edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.edit)
                mListener.editClick(getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {

        void editClick(int position);
    }
}
