package com.example.secretnotes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.secretnotes.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    String mail, pass;
    private static final String TAG = "LoginActivity";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //for the notch
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        sharedPreferences = getSharedPreferences("FireNotesData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        firebaseAuth = FirebaseAuth.getInstance();

        binding.register.setOnClickListener(this);
        binding.loginButton.setOnClickListener(this);
        binding.forgotPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button: {
                // 4. Loading message
                loadDialog();

                mail = binding.usernameTextInput.getText().toString();
                pass = binding.passwordTextInput.getText().toString();

                if (isPasswordValid()) {
                    binding.passwordInputLayout.setError("");
                    if (isEmailValid(mail)) {
                        binding.usernameInputLayout.setError("");

                        firebaseAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    editor.putBoolean("LOGINSTATUS", true);
                                    editor.commit();
                                    Intent intent = new Intent(LoginActivity.this, ContainerActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    editor.putBoolean("LOGINSTATUS", false);
                                    editor.commit();
                                    Log.d(TAG, "ahmed: " + task.getException().getMessage());
                                    String error = task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, "error : " + error, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } else {
                        binding.usernameInputLayout.setError("wrong email");
                    }
                } else {
                    binding.passwordInputLayout.setError("this is a wrong password");
                }

                break;
            }
            case R.id.register: {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.forgot_password: {
                String email = binding.usernameTextInput.getText().toString();
                if (!email.isEmpty() && email != null) {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                Toast.makeText(LoginActivity.this, "Please check your mail", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter your email first", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }

    }

    public void loadDialog() {
        SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
        pDialog.setTitleText("Loading ...");
        pDialog.setCancelable(true);
        pDialog.show();
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
