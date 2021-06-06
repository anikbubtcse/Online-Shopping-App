package com.anik.onlineshoppingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anik.onlineshoppingapp.model.Products;
import com.anik.onlineshoppingapp.model.Users;
import com.anik.onlineshoppingapp.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private TextView closeTextView, updateTextView, changeProfileTextView;
    private EditText nameEt, numberEt, addressEt;
    private CircleImageView imageView;
    private Uri imageUri;
    private Button securityQuestionBtn;
    ProgressDialog loadingBar;
    private DatabaseReference userRef;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        closeTextView = findViewById(R.id.close_settings_TV);
        updateTextView = findViewById(R.id.update_settings_TV);
        changeProfileTextView = findViewById(R.id.profile_image_change);
        nameEt = findViewById(R.id.settings_name_edittext);
        numberEt = findViewById(R.id.settings_number_edittext);
        addressEt = findViewById(R.id.settings_address_edittext);
        imageView = findViewById(R.id.settings_profile_image);
        securityQuestionBtn = findViewById(R.id.security_questions_btn);

        loadingBar = new ProgressDialog(this);
        userRef = FirebaseDatabase.getInstance().getReference();

        showUserInformation();

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),ResetPasswordActivity.class);
                intent.putExtra("check","setting");
                startActivity(intent);

            }
        });

        closeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        changeProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "checked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);

            }
        });

        updateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("")) {

                    updateWithoutImage();

                } else {

                    updateWithImage();
                }

            }

        });

    }

    private void updateWithImage() {

        if (TextUtils.isEmpty(nameEt.getText().toString())) {

            Toast.makeText(getApplicationContext(), "Name is mandatory", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(numberEt.getText().toString())) {

            Toast.makeText(getApplicationContext(), "Number is mandatory", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(addressEt.getText().toString())) {

            Toast.makeText(getApplicationContext(), "Address is mandatory", Toast.LENGTH_LONG).show();
        } else if (imageUri != null) {

            uploadUserImagetoStorage();
        }

    }

    private void uploadUserImagetoStorage() {

        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("User Images");
        StorageReference File_Name = imageRef.child("User Images" + imageUri.getLastPathSegment() + ".JPG");
        File_Name.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                File_Name.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        uploadUserInformationtoDatabase(uri);
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "User Image successfully uploaded to storage", Toast.LENGTH_LONG).show();

                        } else {

                            Toast.makeText(getApplicationContext(), "User Image uploaded to storage failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
    }

    private void uploadUserInformationtoDatabase(Uri uri) {

        loadingBar.setTitle("Update Profile");
        loadingBar.setMessage("Please wait, while updating the profile information");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                HashMap<String, Object> updateInfo = new HashMap();
                updateInfo.put("name", nameEt.getText().toString());
                updateInfo.put("orderNumber", numberEt.getText().toString());
                updateInfo.put("address", addressEt.getText().toString());
                updateInfo.put("image", String.valueOf(uri));

                userRef.child("Users").child(Prevalent.currentOnlineUser.getNumber()).updateChildren(updateInfo)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingBar.dismiss();
                                    Toast.makeText(getApplicationContext(), "User Information successfully updated", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);

                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(getApplicationContext(), "Network error! try again later", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void updateWithoutImage() {

        if (TextUtils.isEmpty(nameEt.getText().toString())) {

            Toast.makeText(getApplicationContext(), "Name is mandatory", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(numberEt.getText().toString())) {

            Toast.makeText(getApplicationContext(), "Number is mandatory", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(addressEt.getText().toString())) {

            Toast.makeText(getApplicationContext(), "Address is mandatory", Toast.LENGTH_LONG).show();
        } else {

            loadingBar.setTitle("Update Profile");
            loadingBar.setMessage("Please wait, while updating the profile");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    HashMap<String, Object> updateInfo = new HashMap();
                    updateInfo.put("name", nameEt.getText().toString());
                    updateInfo.put("orderNumber", numberEt.getText().toString());
                    updateInfo.put("address", addressEt.getText().toString());

                    userRef.child("Users").child(Prevalent.currentOnlineUser.getNumber()).updateChildren(updateInfo)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadingBar.dismiss();
                                        Toast.makeText(getApplicationContext(), "User Information successfully updated", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(intent);

                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(getApplicationContext(), "Network error! try again later", Toast.LENGTH_LONG).show();

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

    private void showUserInformation() {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.child("Users").child(Prevalent.currentOnlineUser.getNumber()).child("image").exists()) {

                    String image = snapshot.child("Users").child(Prevalent.currentOnlineUser.getNumber()).child("image").getValue().toString();
                    String name = snapshot.child("Users").child(Prevalent.currentOnlineUser.getNumber()).child("name").getValue().toString();
                    String phone = snapshot.child("Users").child(Prevalent.currentOnlineUser.getNumber()).child("number").getValue().toString();
                    String address = snapshot.child("Users").child(Prevalent.currentOnlineUser.getNumber()).child("address").getValue().toString();

                    nameEt.setText(name);
                    numberEt.setText(phone);
                    addressEt.setText(address);
                    Picasso.get().load(image).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            imageView.setImageURI(imageUri);
        }
    }
}