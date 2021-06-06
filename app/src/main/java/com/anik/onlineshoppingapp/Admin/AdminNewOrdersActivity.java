package com.anik.onlineshoppingapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.anik.onlineshoppingapp.R;
import com.anik.onlineshoppingapp.model.OrderAdapter;
import com.anik.onlineshoppingapp.model.Orders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView orderRecyclerview;
    private List<Orders> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        initialization();
        getOrderData();
    }

    private void getOrderData() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Orders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()){

                    Orders orderList = ds.getValue(Orders.class);
                    orders.add(orderList);
                    OrderAdapter orderAdapter = new OrderAdapter(orders,AdminNewOrdersActivity.this);
                    orderRecyclerview.setAdapter(orderAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void initialization() {

        orderRecyclerview = findViewById(R.id.admin_new_order_recyclerview);
        orders = new ArrayList<>();
        orderRecyclerview.setLayoutManager(new LinearLayoutManager(this));
    }
}