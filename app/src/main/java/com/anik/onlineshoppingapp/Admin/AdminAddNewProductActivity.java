package com.anik.onlineshoppingapp.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anik.onlineshoppingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, productPrice, productName, productDescription, currentDate, currentTime, productRandomKey;
    private ImageView inputProductImage;
    EditText inputProductName, inputProductPrice, inputProductDescription;
    private Button addNewProductButton;
    private Uri imageUri;
    private ProgressDialog loadingBar;
    private StorageReference reference;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        reference = FirebaseStorage.getInstance().getReference().child("Product Images");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        categoryName = getIntent().getStringExtra("category");

        inputProductImage = findViewById(R.id.select_product_image);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);
        addNewProductButton = findViewById(R.id.add_new_product);
        loadingBar = new ProgressDialog(this);

        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImageFromGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productDataValidation();
            }
        });

    }

    private void productDataValidation() {

        productName = inputProductName.getText().toString();
        productDescription = inputProductDescription.getText().toString();
        productPrice = inputProductPrice.getText().toString();

        if (imageUri != null) {
            if (!TextUtils.isEmpty(productName)) {
                if (!TextUtils.isEmpty(productDescription)) {
                    if (!TextUtils.isEmpty(productPrice)) {

                        loadingBar.setTitle("Upload image");
                        loadingBar.setMessage("Please wait while uploading the image ...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        storeImageData();

                    } else {

                        Toast.makeText(getApplicationContext(), "Product description can not be empty", Toast.LENGTH_LONG).show();
                    }

                } else {

                    Toast.makeText(getApplicationContext(), "Product description can not be empty", Toast.LENGTH_LONG).show();
                }

            } else {

                Toast.makeText(getApplicationContext(), "Product name can not be empty", Toast.LENGTH_LONG).show();
            }


        } else {

            Toast.makeText(getApplicationContext(), "Product image can not be empty", Toast.LENGTH_LONG).show();
        }
    }

    private void storeImageData() {

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = simpleDateFormat.format(calender.getTime());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss a");
        currentTime = simpleDateFormat1.format(calender.getTime());
        productRandomKey = currentDate + " " + currentTime;

        StorageReference File_Name = reference.child("Product Images" + imageUri.getLastPathSegment() + ".JPG");
        File_Name.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                File_Name.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        saveProductInformation(uri);


                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(), "Image successfully uploaded to firebase storage", Toast.LENGTH_LONG).show();
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(), "Sorry, image uploading to firebase storage failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });

    }

    private void saveProductInformation(Uri uri) {

        HashMap<String, Object> productInformation = new HashMap<>();
        productInformation.put("pid", productRandomKey);
        productInformation.put("date", currentDate);
        productInformation.put("time", currentTime);
        productInformation.put("image", String.valueOf(uri));
        productInformation.put("name", productName);
        productInformation.put("description", productDescription);
        productInformation.put("price", productPrice);
        productInformation.put("category", categoryName);

        databaseReference.child("Product Information").child(productRandomKey).updateChildren(productInformation)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(), "product Information is successfully uploaded to firebase database", Toast.LENGTH_LONG).show();

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(getApplicationContext(), "Sorry, product information uploading to firebase database failed", Toast.LENGTH_LONG).show();

                        }

                    }
                });

    }

    private void getImageFromGallery() {

        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageIntent.setType("image/*");
        startActivityForResult(imageIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            inputProductImage.setImageURI(imageUri);
        }

    }
}