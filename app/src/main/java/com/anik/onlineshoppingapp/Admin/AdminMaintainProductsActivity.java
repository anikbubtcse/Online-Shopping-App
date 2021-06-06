package com.anik.onlineshoppingapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.anik.onlineshoppingapp.HomeActivity;
import com.anik.onlineshoppingapp.R;
import com.anik.onlineshoppingapp.model.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private ImageView maintainIv;
    private EditText nameEt, priceEt, descriptionEt;
    private Button editButton;
    private String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        pid = getIntent().getStringExtra("pid");

        Init();
        showProductsDetails();
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editProductInfo();
            }
        });
    }

    private void editProductInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", nameEt.getText().toString());
                hashMap.put("price", priceEt.getText().toString());
                hashMap.put("description", descriptionEt.getText().toString());

                reference.child("Product Information").child(pid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Product information is successfully edited", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                        } else {

                            Toast.makeText(getApplicationContext(), "Network problem! try again later", Toast.LENGTH_LONG).show();


                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void showProductsDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.child("Product Information").child(pid).exists()) {

                    Products product = snapshot.child("Product Information").child(pid).getValue(Products.class);
                    nameEt.setText(product.getName());
                    nameEt.setSelection(nameEt.getText().length());
                    priceEt.setText(product.getPrice());
                    priceEt.setSelection(priceEt.getText().length());
                    descriptionEt.setText(product.getDescription());
                    descriptionEt.setSelection(descriptionEt.getText().length());
                    Picasso.get().load(product.getImage()).into(maintainIv);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void Init() {

        maintainIv = findViewById(R.id.maintain_image_view);
        nameEt = findViewById(R.id.maintain_name_Et);
        priceEt = findViewById(R.id.maintain_price_Et);
        descriptionEt = findViewById(R.id.maintain_description_Et);
        editButton = findViewById(R.id.apply_change_Btn);
    }
}