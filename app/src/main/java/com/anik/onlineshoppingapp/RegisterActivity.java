package com.anik.onlineshoppingapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, numberEditText, passwordEditText;
    private Button registerButton;
    private ProgressDialog loadingbar;

    String databaseParent = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.register_name_input);
        numberEditText = findViewById(R.id.register_phone_number_input);
        passwordEditText = findViewById(R.id.register_password_input);
        registerButton = findViewById(R.id.register_btn);
        loadingbar = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createAccount();
            }
        });
    }

    private void createAccount() {

        String name = nameEditText.getText().toString();
        String number = numberEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {

            Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(number)) {

            Toast.makeText(getApplicationContext(), "Please enter your number", Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(password)) {

            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_LONG).show();

        } else {

            if (password.length() >= 6) {

                loadingbar.setTitle("Create Account");
                loadingbar.setMessage("Please wait, while checking the credentials");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                validatePhoneNumber(name, number, password);

            } else {

                Toast.makeText(getApplicationContext(), "Password is too short! must be at least 6 characters", Toast.LENGTH_LONG).show();
            }

        }
    }

    private void validatePhoneNumber(String name, String number, String password) {

        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (!(snapshot.child(databaseParent).child(number).exists())) {

                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("name", name);
                    userData.put("number", number);
                    userData.put("password", password);
                    reference.child(databaseParent).child(number).updateChildren(userData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        loadingbar.dismiss();
                                        Toast.makeText(getApplicationContext(), "Congratulations! your account has successfully been created", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);

                                    } else {
                                        loadingbar.dismiss();
                                        Toast.makeText(getApplicationContext(), "Network error! please try after sometime", Toast.LENGTH_LONG).show();

                                    }

                                }
                            });

                } else {
                    loadingbar.dismiss();
                    Toast.makeText(getApplicationContext(), "You already have an account with this number", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}