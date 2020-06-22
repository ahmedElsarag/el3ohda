package com.example.secretnotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secretnotes.R;
import com.example.secretnotes.data.UserNote;
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
        View view = LayoutInflater.from(context).inflate(R.layout.card_note,parent,false);
        return new NotesHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {

        if (isShimmer){
            holder.shimmerFrameLayout.startShimmer();
        }else {
            holder.shimmerFrameLayout.stopShimmer();
            holder.shimmerFrameLayout.setShimmer(null);
            holder.title.setText(list.get(position).getNoteTitle());
            holder.title.setBackground(null);
            holder.desc.setText(list.get(position).getNoteDesc());
            holder.desc.setBackground(null);
            holder.date.setText(list.get(position).getNoteDate());
            holder.date.setBackground(null);
        }



    }

    @Override
    public int getItemCount() {
        return isShimmer?shimmerNum:list.size();
    }

    public class NotesHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title , desc , date;
        ImageView edit ;
        ShimmerFrameLayout shimmerFrameLayout;
        private RecyclerViewClickListener mListener;

        public NotesHolder(@NonNull View itemView ,RecyclerViewClickListener mListener) {
            super(itemView);
            this.mListener = mListener;
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            date = itemView.findViewById(R.id.date);
            edit = itemView.findViewById(R.id.edit);
            shimmerFrameLayout = itemView.findViewById(R.id.shimmer_layout);
            edit.setOnClickListener(this);
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
