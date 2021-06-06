package com.anik.onlineshoppingapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.anik.onlineshoppingapp.R;
import com.anik.onlineshoppingapp.model.ProductAdapter;
import com.anik.onlineshoppingapp.model.UserCart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdminUserProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<UserCart> products;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        number = getIntent().getStringExtra("number");

        initialization();
        showOrderProducts();
    }

    private void showOrderProducts() {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(number).child("Products");

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){

                        UserCart cart = ds.getValue(UserCart.class);
                        products.add(cart);
                        ProductAdapter productAdapter = new ProductAdapter(products);
                        recyclerView.setAdapter(productAdapter);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void initialization() {

        recyclerView = findViewById(R.id.product_recycler_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        products = new ArrayList<>();
    }
}