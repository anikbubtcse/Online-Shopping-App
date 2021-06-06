package com.anik.onlineshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anik.onlineshoppingapp.model.Products;
import com.anik.onlineshoppingapp.prevalent.Prevalent;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView productNameDeails, productDescriptionDetails, productPriceDetails;
    private ElegantNumberButton elegantNumberButton;
    private ImageView productImageDetails;
    private Button cartButton;
    private String pid, state = "normal";
    private Products products;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productNameDeails = findViewById(R.id.product_name_details);
        productDescriptionDetails = findViewById(R.id.product_description_details);
        productPriceDetails = findViewById(R.id.product_price_details);
        productImageDetails = findViewById(R.id.product_image_details);
        elegantNumberButton = findViewById(R.id.elegant_number_button);
        cartButton = findViewById(R.id.add_to_cart_button);


        rootRef = FirebaseDatabase.getInstance().getReference();
        pid = getIntent().getStringExtra("pid");
        showData(pid);

        checkOrderState();

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (state.equals("order shipped") || state.equals("order placed")) {

                    Toast.makeText(getApplicationContext(), "You will only able to add products to the cart once your previous order is shipped or confirmed", Toast.LENGTH_LONG).show();
                } else {

                    addingToCart();
                }

            }
        });


    }

    private void checkOrderState() {

        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getNumber());
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String shippingState = snapshot.child("state").getValue().toString();

                    if (shippingState == "shipped") {

                        state = "order shipped";


                    } else {

                        state = "order placed";

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void addingToCart() {

        final DatabaseReference cartRef;
        cartRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss a");
        String currentTime = simpleDateFormat1.format(calendar.getTime());

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                HashMap<String, Object> hashCart = new HashMap<>();
                hashCart.put("pid", pid);
                hashCart.put("date", currentDate);
                hashCart.put("time", currentTime);
                hashCart.put("pname", productNameDeails.getText().toString());
                hashCart.put("price", products.getPrice());
                hashCart.put("quantity", elegantNumberButton.getNumber());
                hashCart.put("discount", "");

                cartRef.child("User View").child(Prevalent.currentOnlineUser.getNumber())
                        .child("Products").child(pid).updateChildren(hashCart).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            cartRef.child("Admin View").child(Prevalent.currentOnlineUser.getNumber())
                                    .child("Products").child(pid).updateChildren(hashCart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(getApplicationContext(), "Added to cart list", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(intent);
                                    }

                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void showData(String pid) {

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.child("Product Information").child(pid).exists()) {

                    products = snapshot.child("Product Information").child(pid).getValue(Products.class);
                    productNameDeails.setText(products.getName());
                    productDescriptionDetails.setText(products.getDescription());
                    productPriceDetails.setText(products.getPrice() + " " + "$");
                    Picasso.get().load(products.getImage()).into(productImageDetails);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}