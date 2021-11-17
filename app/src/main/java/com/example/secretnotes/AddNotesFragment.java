package com.example.secretnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secretnotes.data.FavNotes;
import com.example.secretnotes.data.UserNote;
import com.example.secretnotes.databinding.FragmentAddNotesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.CompletableObserver;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNotesFragment extends Fragment implements View.OnClickListener {

    FragmentAddNotesBinding binding;
    DatabaseReference databaseNotesReference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String uId;
    boolean isLiked;
    int afterUpdateTotal,afterUpdateDepart;
    SweetAlertDialog pDialog;

    public AddNotesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
           binding = FragmentAddNotesBinding.inflate(getLayoutInflater());

        sharedPreferences = getActivity().getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        uId = sharedPreferences.getString("UID","");

        binding.saveBtn.setOnClickListener(this);
        binding.cancelBtn.setOnClickListener(this);

        databaseNotesReference = FirebaseDatabase.getInstance().getReference("USERNOTES").child(uId);

        if (!Variables.isAdd){
            binding.notesTitle.setText(HomeFragment.allNotes.get(Variables.pos).getNoteTitle());
            binding.notesDesc.setText(HomeFragment.allNotes.get(Variables.pos).getNoteDesc());
            binding.totalAmount.setText(HomeFragment.allNotes.get(Variables.pos).getTotalAmount());
            binding.currentAmount.setText(HomeFragment.allNotes.get(Variables.pos).getCurrentAmount());
            isLiked = HomeFragment.allNotes.get(Variables.pos).isLiked();
            binding.addedTotal.setVisibility(View.VISIBLE);
            binding.addedAmount.setVisibility(View.VISIBLE);

        }
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_btn:{
                String title = binding.notesTitle.getText().toString();
                String desc = binding.notesDesc.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
                Calendar calendar = Calendar.getInstance();
                String todayDate = dateFormat.format(calendar.getTime());
                String totalAmount = binding.totalAmount.getText().toString();
                String currentAmount = binding.currentAmount.getText().toString();

                if(!Variables.isAdd){
                    String addedAmount = binding.addedAmount.getText().toString();
                    String addedTotal = binding.addedTotal.getText().toString();
                    if (!addedTotal.isEmpty() && addedTotal!=null){
                        afterUpdateTotal = Integer.parseInt(totalAmount)+Integer.parseInt(addedTotal);
                    }else {
                        afterUpdateTotal = Integer.parseInt(totalAmount);
                    }

                    if (!addedAmount.isEmpty() && addedAmount!=null){
                        afterUpdateDepart = Integer.parseInt(currentAmount)+Integer.parseInt(addedAmount);
                    }else {
                        afterUpdateDepart = Integer.parseInt(currentAmount);
                    }

                    String key = HomeFragment.allNotes.get(Variables.pos).getNoteID();
                    // TODO: 8/5/2021 change false value
                    UserNote userNote = new UserNote(title,desc,todayDate,key,Integer.toString(afterUpdateTotal),Integer.toString(afterUpdateDepart),isLiked);
                    loadDialog();
                    updaeNote(userNote,key);

                }else{
                    if(!title.equalsIgnoreCase("") && !desc
                            .equalsIgnoreCase("")){
                        String key = databaseNotesReference.push().getKey();
                        UserNote userNote = new UserNote(title,desc,todayDate,key,totalAmount,currentAmount,false);
                        loadDialog();
                        addNotes(key,userNote);

                    }else {
                        Toast.makeText(getContext(),"قم بادخال جميع البيانات المطلوبة",Toast.LENGTH_LONG).show();
                    }
                }

                break;
            }
            case R.id.cancel_btn:{
                ((NavigationHost)getActivity()).navigateTo(new HomeFragment(),false);
                break;
            }
        }
    }

    public void addNotes(String key,UserNote userNote){

        databaseNotesReference.child(key).setValue(userNote).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(),"تمت الأضافة بنجاح",Toast.LENGTH_LONG).show();
                    ((NavigationHost)getActivity()).navigateTo(new HomeFragment(),false);
                    pDialog.dismissWithAnimation();
                }else {
                    Toast.makeText(getContext(),"حدث خطأ لم تتم الأضافة",Toast.LENGTH_LONG).show();
                    pDialog.dismissWithAnimation();
                }
            }
        });
    }

    public void updaeNote(UserNote updatedNote ,String key){
        databaseNotesReference.child(key).setValue(updatedNote).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "تم التعديل بنجاح", Toast.LENGTH_LONG).show();
                    ((NavigationHost)getActivity()).navigateTo(new HomeFragment(),false);
                    pDialog.dismissWithAnimation();
                } else {
                    Toast.makeText(getContext(), "حدث خطأ لم يتم التعديل", Toast.LENGTH_LONG).show();
                    pDialog.dismissWithAnimation();
                }
            }
        });
    }

    public void loadDialog() {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("جاري الأضافة ...");
        pDialog.show();
    }
}
