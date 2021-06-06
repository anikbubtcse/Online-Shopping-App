package com.anik.onlineshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anik.onlineshoppingapp.model.Questions;
import com.anik.onlineshoppingapp.model.Users;
import com.anik.onlineshoppingapp.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextView headerTitle, titleQuestions;
    private EditText phoneNumber, answer1, answer2;
    private Button verifyBtn;
    private String check = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        headerTitle = findViewById(R.id.header_title);
        titleQuestions = findViewById(R.id.title_questions);
        phoneNumber = findViewById(R.id.find_phone_number);
        answer1 = findViewById(R.id.question_1);
        answer2 = findViewById(R.id.question_2);
        verifyBtn = findViewById(R.id.verify_btn);

        check = getIntent().getStringExtra("check");

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (check.equals("setting")) {

            headerTitle.setText("Set Security Questions");
            titleQuestions.setText("Please, enter the following security questions for reset password");
            verifyBtn.setText("Set");
            phoneNumber.setVisibility(View.GONE);
            showExistingAnswers();
            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    setSecurityQuestions();
                }
            });

        } else if (check.equals("login")) {

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String question1 = answer1.getText().toString().toLowerCase();
                    String question2 = answer2.getText().toString().toLowerCase();
                    String number = phoneNumber.getText().toString();

                    if (TextUtils.isEmpty(question1)) {

                        Toast.makeText(getApplicationContext(), "Please enter the first security answer", Toast.LENGTH_LONG).show();

                    } else if (TextUtils.isEmpty(question2)) {

                        Toast.makeText(getApplicationContext(), "Please enter the second security answer", Toast.LENGTH_LONG).show();

                    } else if (TextUtils.isEmpty(number)) {

                        Toast.makeText(getApplicationContext(), "Please enter the phone number", Toast.LENGTH_LONG).show();

                    } else {

                        verify(question1, question2, number);
                    }
                }
            });

        }
    }

    private void verify(String question1, String question2, String number) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.child("Users").child(number).exists()) {

                    Questions questions = snapshot.child("Users").child(number).child("Security Questions").getValue(Questions.class);

                    String response1 = questions.getAnswer1();
                    String response2 = questions.getAnswer2();

                    if (!response1.equals(question1)) {

                        Toast.makeText(getApplicationContext(), response1, Toast.LENGTH_LONG).show();

                    } else if (!response2.equals(question2)) {

                        Toast.makeText(getApplicationContext(), "Sorry! second answer does not match", Toast.LENGTH_LONG).show();

                    } else if (question1.equals(response1) && question2.equals(response2)) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                        builder.setTitle("Change Password");
                        final EditText editText = new EditText(ResetPasswordActivity.this);
                        editText.setHint("Enter the new Password");
                        editText.setMaxLines(1);
                        builder.setView(editText);
                        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String password = editText.getText().toString();

                                if (TextUtils.isEmpty(password)) {

                                    Toast.makeText(getApplicationContext(), "Please enter the new password", Toast.LENGTH_LONG).show();

                                } else if (password.length() < 6) {

                                    Toast.makeText(getApplicationContext(), "Password length can not be less than 6", Toast.LENGTH_LONG).show();

                                } else {

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("password", password);
                                            reference.child("Users").child(number).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(getApplicationContext(), "Your password has been successfully updated", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);

                            }
                        });
                        builder.show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Account does not exist with this number", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    private void setSecurityQuestions() {

        String questions1 = answer1.getText().toString().toLowerCase();
        String questions2 = answer2.getText().toString().toLowerCase();

        if (questions1.equals("")) {

            Toast.makeText(getApplicationContext(), "Please enter the first security answer", Toast.LENGTH_LONG).show();

        }
        if (questions2.equals("")) {

            Toast.makeText(getApplicationContext(), "Please enter the second security answer", Toast.LENGTH_LONG).show();

        } else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    Users user = snapshot.child("Users").child(Prevalent.currentOnlineUser.getNumber()).getValue(Users.class);
                    if (snapshot.child("Users").child(user.getNumber()).exists()) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("answer1", questions1);
                        hashMap.put("answer2", questions2);
                        reference.child("Users").child(user.getNumber()).child("Security Questions").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(getApplicationContext(), "Security questions are successfully set up", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);

                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }

    private void showExistingAnswers() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                Users user = snapshot.child("Users").child(Prevalent.currentOnlineUser.getNumber()).getValue(Users.class);

                if (snapshot.child("Users").child(user.getNumber()).child("Security Questions").exists()) {

                    String question1 = (String) snapshot.child("Users").child(user.getNumber()).child("Security Questions").child("answer1").getValue();
                    String question2 = (String) snapshot.child("Users").child(user.getNumber()).child("Security Questions").child("answer2").getValue();
                    answer1.setText(question1);
                    answer2.setText(question2);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}