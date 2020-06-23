package com.example.secretnotes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secretnotes.data.UserNote;
import com.example.secretnotes.databinding.FragmentAddNotesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddNotesFragment extends Fragment implements View.OnClickListener {

    FragmentAddNotesBinding binding;
    DatabaseReference databaseNotesReference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String uId;

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


                if(!Variables.isAdd){
                    String key = HomeFragment.allNotes.get(Variables.pos).getNoteID();
                    UserNote userNote = new UserNote(title,desc,todayDate,key);
                    updaeNote(userNote,key);
                }else{
                    if(!title.equalsIgnoreCase("") && !desc
                            .equalsIgnoreCase("")){
                        String key = databaseNotesReference.push().getKey();
                        UserNote userNote = new UserNote(title,desc,todayDate,key);
                        addNotes(key,userNote);

                    }else {
                        Toast.makeText(getContext(),"make sure to fill all fieldes",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getContext(),"note saved successfully",Toast.LENGTH_LONG).show();
                    ((NavigationHost)getActivity()).navigateTo(new HomeFragment(),false);
                }else {
                    Toast.makeText(getContext(),"there is an error occuared",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updaeNote(UserNote updatedNote ,String key){
        databaseNotesReference.child(key).setValue(updatedNote).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_LONG).show();
                    ((NavigationHost)getActivity()).navigateTo(new HomeFragment(),false);
                } else {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
