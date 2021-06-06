package com.anik.onlineshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anik.onlineshoppingapp.model.CartAdapter;
import com.anik.onlineshoppingapp.model.Products;
import com.anik.onlineshoppingapp.model.UserCart;
import com.anik.onlineshoppingapp.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.cache.DiskLruCache;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView totalPrice,textMessage;
    private Button nextButton;
    private int tPrice = 0;
    private List<UserCart> cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        totalPrice = findViewById(R.id.cart_total_price_id);
        nextButton = findViewById(R.id.cart_next_id);
        recyclerView = findViewById(R.id.cart_recycler_id);
        textMessage = findViewById(R.id.msg_text_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartList = new ArrayList<>();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("tPrice",String.valueOf(tPrice));
                startActivity(intent);
            }
        });

        showCartDetails();
        checkOrderState();

    }

    private void checkOrderState() {

        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getNumber());
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               if(snapshot.exists()){

                   String shippingState = snapshot.child("state").getValue().toString();
                   String name = snapshot.child("name").getValue().toString();

                   if(shippingState.equals("shipped")){

                       totalPrice.setText("State = Shipped");
                       recyclerView.setVisibility(View.GONE);
                       textMessage.setVisibility(View.VISIBLE);
                       textMessage.setText("Dear"+" "+name+" "+"your products has been shipped");
                       nextButton.setVisibility(View.GONE);

                   }else if(shippingState.equals("not shipped")){

                       totalPrice.setText("State = not shipped");
                       recyclerView.setVisibility(View.GONE);
                       textMessage.setVisibility(View.VISIBLE);
                       nextButton.setVisibility(View.GONE);

                   }
               }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void showCartDetails() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View")
                .child(Prevalent.currentOnlineUser.getNumber()).child("Products");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    UserCart cart = ds.getValue(UserCart.class);

                    int quantity = Integer.parseInt(cart.getQuantity());
                    int price = Integer.parseInt(cart.getPrice());
                    tPrice = (tPrice + (price * quantity));
                    cartList.add(cart);
                    CartAdapter adapter = new CartAdapter(cartList, CartActivity.this);
                    recyclerView.setAdapter(adapter);
                }

                totalPrice.setText("Total Price = "+String.valueOf(tPrice)+" "+"$");

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
}