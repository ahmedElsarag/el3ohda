package com.example.secretnotes.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    List<UserNote> list;
    private RecyclerViewClickListener listener;
    public boolean isShimmer = true;
    int shimmerNum = 5;
    String residal;


    public NotesAdapter(Context context, List<UserNote> list, RecyclerViewClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        CardNoteBinding binding = DataBindingUtil.inflate(inflater, R.layout.card_note, parent, false);
        return new NotesHolder(binding, listener);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {


        if (isShimmer) {
            holder.binding.shimmerLayout.startShimmer();
        } else {
            //-------------------------------------------------------
            String total=list.get(position).getTotalAmount();
            String depart=list.get(position).getCurrentAmount();
            int totalAmount = Integer.parseInt(total.replaceAll("\\s+",""));
            int departedAmount = Integer.parseInt(depart.replaceAll("\\s+",""));
            int residualAmount = totalAmount - departedAmount;
            residal = "" + residualAmount;
            //-------------------------------------------------
            holder.binding.shimmerLayout.stopShimmer();
            holder.binding.shimmerLayout.setShimmer(null);
            holder.binding.items.setVisibility(View.VISIBLE);
            holder.binding.title.setText(list.get(position).getNoteTitle());
            holder.binding.title.setBackground(null);
            holder.binding.desc.setText(list.get(position).getNoteDesc());
            holder.binding.desc.setBackground(null);
            // holder.binding.date.setText(list.get(position).getNoteDate());
            holder.binding.date.setBackground(null);
            holder.binding.totalAmount.setText(convertTOArabicNumber("" + totalAmount));
            holder.binding.residual.setText(convertTOArabicNumber("" + departedAmount));
            holder.binding.departed.setText(convertTOArabicNumber(residal));

            // if (position == 0){

            //   LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            //        LinearLayout.LayoutParams.MATCH_PARENT,
            //      LinearLayout.LayoutParams.WRAP_CONTENT
            //);
            //params.setMargins(0, 50, 0, 0);
            //holder.binding.noteItem.setLayoutParams(params);
            //}

            if (list.get(position).isLiked())
                holder.binding.edit.setImageResource(R.drawable.ic_favorite_black_24dp);
            if (isLow(position)) {
                holder.binding.animationView.setVisibility(View.VISIBLE);
            }

        }


    }

    @Override
    public int getItemCount() {
        return isShimmer ? shimmerNum : list.size();
    }

    //---------------------------------------------------------
    public class NotesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardNoteBinding binding;
        private RecyclerViewClickListener mListener;

        public NotesHolder(@NonNull CardNoteBinding binding, RecyclerViewClickListener mListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.mListener = mListener;
            binding.edit.setOnClickListener(this);
            binding.noteItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.note_item)
                mListener.editClick(getAdapterPosition());
            else
                mListener.addToFavorit(getAdapterPosition(), binding.edit);
        }
    }

    public interface RecyclerViewClickListener {

        void editClick(int position);

        void addToFavorit(int position, ImageView imageView);
    }

    Boolean isLow(int position) {
        Log.d("TAG2", "position =" + position);
        Log.d("TAG", "index=" + list.get(position).getNoteTitle() + "num");
        String totalAm=list.get(position).getTotalAmount();
        int total = Integer.parseInt( totalAm.replaceAll("\\s+",""));
        int current = Integer.parseInt(residal);
        if (current == total) {
            return false;
        } else {

            if (current <= (total / 4))
                return true;
            else
                return false;
        }
    }

    String convertTOArabicNumber(String str) {
        int arabic_zero_unicode = 1632;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < str.length(); ++i) {
            builder.append((char) ((int) str.charAt(i) - 48 + arabic_zero_unicode));
        }
        return builder.toString();
    }
}
