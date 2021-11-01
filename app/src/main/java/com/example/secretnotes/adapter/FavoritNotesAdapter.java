package com.example.secretnotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secretnotes.FavNotesDatabase;
import com.example.secretnotes.R;
import com.example.secretnotes.data.FavNotes;
import com.example.secretnotes.data.UserNote;
import com.example.secretnotes.databinding.CardNoteBinding;
import com.example.secretnotes.databinding.FavCardBinding;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FavoritNotesAdapter extends RecyclerView.Adapter<FavoritNotesAdapter.FavoritNotesHolder>{

    Context context;
    List<FavNotes> list = new ArrayList<>();
    public boolean isShimmer = false;
    int shimmerNum = 5;
    String departed;


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
        FavCardBinding binding = DataBindingUtil.inflate(layoutInflater,R.layout.fav_card,parent,false);
        return new FavoritNotesHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritNotesHolder holder, final int position) {

        if (isShimmer){
            holder.binding.shimmerLayout.startShimmer();
        }else {
            //-------------------------------------------------------
            String total=list.get(position).getTotalAmount();
            String depart=list.get(position).getDepartedAmount();
            int totalAmount = Integer.parseInt(total.replaceAll("\\s+",""));
            int departedAmount = Integer.parseInt(depart.replaceAll("\\s+",""));
            int residualAmount = totalAmount - departedAmount;
            departed = "" + residualAmount;
            //-------------------------------------------------
            holder.binding.shimmerLayout.stopShimmer();
            holder.binding.shimmerLayout.setShimmer(null);
            holder.binding.title.setText(list.get(position).getNoteTitle());
            holder.binding.title.setBackground(null);
            holder.binding.desc.setText(list.get(position).getNoteDesc());
            holder.binding.desc.setBackground(null);
            holder.binding.date.setBackground(null);
            holder.binding.totalAmount.setText(list.get(position).getTotalAmount());
            holder.binding.residual.setText(list.get(position).getDepartedAmount());
            holder.binding.departed.setText(departed);
        }
    }

    @Override
    public int getItemCount() {
        return isShimmer ? shimmerNum : list.size();
    }

    public class FavoritNotesHolder extends RecyclerView.ViewHolder{
        FavCardBinding binding;
        public FavoritNotesHolder(@NonNull FavCardBinding binding ) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
