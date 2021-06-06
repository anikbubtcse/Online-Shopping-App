package com.anik.onlineshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.anik.onlineshoppingapp.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText nameEditTv, numberEditTv, addressEditTv, cityEditTv;
    private Button confirmButton;
    private String totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        nameEditTv = findViewById(R.id.shipment_name);
        numberEditTv = findViewById(R.id.shipment_number);
        addressEditTv = findViewById(R.id.shipment_address);
        cityEditTv = findViewById(R.id.shipment_city);
        confirmButton = findViewById(R.id.shipment_confirm_button);

        totalPrice = getIntent().getStringExtra("tPrice");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calender = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                String currentDate = simpleDateFormat.format(calender.getTime());
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss a");
                String currentTime = simpleDateFormat1.format(calender.getTime());

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        HashMap<String, Object> orderMap = new HashMap<>();
                        orderMap.put("tprice", totalPrice);
                        orderMap.put("time", currentTime);
                        orderMap.put("date", currentDate);
                        orderMap.put("name", nameEditTv.getText().toString());
                        orderMap.put("number", numberEditTv.getText().toString());
                        orderMap.put("address", addressEditTv.getText().toString());
                        orderMap.put("city", cityEditTv.getText().toString());
                        orderMap.put("state", "not shipped");

                        databaseReference.child("Orders")
                                .child(Prevalent.currentOnlineUser.getNumber())
                                .updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    databaseReference.child("Cart List")
                                            .child("User View")
                                            .child(Prevalent.currentOnlineUser.getNumber())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                Toast.makeText(getApplicationContext(), "Your final order has been placed successfully", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        });
    }
}