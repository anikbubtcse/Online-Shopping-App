package com.anik.onlineshoppingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.rey.material.widget.CheckBox;

import org.jetbrains.annotations.NotNull;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText numberInput, passwordInput;
    private Button loginButton;
    private ProgressDialog loadingBar;
    private CheckBox checkBoxRememberMe;
    private TextView adminLink, notAdminLink,forgetPassword;
    private String databaseParent = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        numberInput = findViewById(R.id.login_phone_number_input);
        passwordInput = findViewById(R.id.login_password_input);
        loginButton = findViewById(R.id.login_btn);
        checkBoxRememberMe = findViewById(R.id.remember_me_chkb);
        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.not_admin_panel_link);
        forgetPassword = findViewById(R.id.forgot_password_link);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAccount();
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setText("Admin Login");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                databaseParent = "Admins";

            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                databaseParent = "Users";

            }
        });

    }

    private void loginAccount() {
        String number = numberInput.getText().toString();
        String password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(number)) {

            Toast.makeText(getApplicationContext(), "Please enter your number", Toast.LENGTH_LONG).show();

        } else if (TextUtils.isEmpty(password)) {

            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_LONG).show();

        } else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            accessAccount(number, password);
        }

    }

    private void accessAccount(String number, String password) {
        DatabaseReference reference;
        Paper.book().write(Prevalent.databaseParentKey, databaseParent);
        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.child(databaseParent).child(number).exists()) {
                        Users user = snapshot.child(databaseParent).child(number).getValue(Users.class);
                    if (user.getNumber().equals(number)) {
                        if (user.getPassword().equals(password)) {
                            if (checkBoxRememberMe.isChecked()) {
                                Paper.book().write(Prevalent.numberInputKey, number);
                                Paper.book().write(Prevalent.passwordInputKey, password);
                            }
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

                            } else {

                                loadingBar.dismiss();
                                Toast.makeText(getApplicationContext(), "Sorry! network problem", Toast.LENGTH_LONG).show();
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