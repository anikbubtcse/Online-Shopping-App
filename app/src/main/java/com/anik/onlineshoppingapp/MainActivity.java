package com.anik.onlineshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anik.onlineshoppingapp.Admin.AdminCategoryActivity;
import com.anik.onlineshoppingapp.model.Users;
import com.anik.onlineshoppingapp.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button mainRegisterButton, mainLoginButton;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainRegisterButton = findViewById(R.id.main_join_now_btn);
        mainLoginButton = findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        mainLoginButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });

        mainRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        String number = Paper.book().read(Prevalent.numberInputKey);
        String password = Paper.book().read(Prevalent.passwordInputKey);
        String databaseParent = Paper.book().read(Prevalent.databaseParentKey);

        if (number != "" && password != "" && databaseParent != "") {
            if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(databaseParent)) {

                loadingBar.setTitle("Already logged in");
                loadingBar.setMessage("Please wait.........");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                accessAccount(number, password, databaseParent);
            }

        }

    }

    private void accessAccount(String number, String password, String databaseParent) {
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.child(databaseParent).child(number).exists()) {
                    Users user = snapshot.child(databaseParent).child(number).getValue(Users.class);
                    if (user.getNumber().equals(number)) {
                        if (user.getPassword().equals(password)) {
                            if (databaseParent.equals("Users")) {
                                loadingBar.dismiss();
                                Toast.makeText(getApplicationContext(), "Congratulations user! login sucessfull", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                Prevalent.currentOnlineUser = user;
                                startActivity(intent);

                            } else if (databaseParent.equals("Admins")) {
                                loadingBar.dismiss();
                                Toast.makeText(getApplicationContext(), "Congratulations Admin! login sucessfull", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), AdminCategoryActivity.class);
                                startActivity(intent);

                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(), "Sorry! Invalid password", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(getApplicationContext(), "Account with this phone number does not exist", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

}
