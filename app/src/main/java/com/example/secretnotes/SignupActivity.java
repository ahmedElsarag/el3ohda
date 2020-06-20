package com.example.secretnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.secretnotes.data.UserInfo;
import com.example.secretnotes.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignupBinding binding;
    FirebaseAuth firebaseAuth;
    DatabaseReference usersDatabaseRef;
    String mail, pass, name;
    private static final String TAG = "SignupActivity";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        sharedPreferences = getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        firebaseAuth = FirebaseAuth.getInstance();
        usersDatabaseRef = FirebaseDatabase.getInstance().getReference("USERS");

        binding.registerButton.setOnClickListener(this);
        binding.login.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_button: {
                name = binding.name.getText().toString();
                mail = binding.usernameTextInput.getText().toString();
                pass = binding.passwordTextInput.getText().toString();
                if (isPasswordValid()) {
                    binding.passwordInputLayout.setError("");
                    if (isEmailValid(mail)) {
                        binding.usernameInputLayout.setError("");
                        firebaseAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                //save user info
                                {
                                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                    final String userId = firebaseUser.getUid();
                                    usersDatabaseRef.child(userId).setValue(new UserInfo(name, mail, "")).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                editor.putString("UID", userId);
                                                editor.commit();
                                                Toast.makeText(SignupActivity.this, "done", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "ahmed: error_ " + task.getResult());
                                    Toast.makeText(SignupActivity.this, "error", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    } else {
                        binding.usernameInputLayout.setError("this is not correct mail");
                    }

                } else {
                    binding.passwordInputLayout.setError("password must be 6 charachter or more");
                }

                break;
            }
            case R.id.login: {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }

    }

    public Boolean isPasswordValid() {
        if (pass.length() < 6)
            return false;
        else
            return true;
    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }
}
