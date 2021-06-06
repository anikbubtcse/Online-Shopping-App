package com.anik.onlineshoppingapp.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.onlineshoppingapp.CartActivity;
import com.anik.onlineshoppingapp.HomeActivity;
import com.anik.onlineshoppingapp.ProductDetailsActivity;
import com.anik.onlineshoppingapp.R;
import com.anik.onlineshoppingapp.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private List<UserCart> cartList;
    private Context context;
    private int totalPrice = 0;

    public CartAdapter(List<UserCart> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_item_design, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CartAdapter.MyViewHolder holder, int position) {

        UserCart cart = cartList.get(position);
        holder.cartProductName.setText(cart.getPname());
        holder.cartProductPrice.setText("Price = " + cart.getPrice() +" "+ "$");
        holder.cartProductQuantity.setText("Quantity = " + cart.getQuantity());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                CharSequence[] sequence = new CharSequence[2];
                sequence[0] = "Edit";
                sequence[1] = "Remove";

                builder.setTitle("Cart options:");
                builder.setIcon(R.drawable.alert_icon);
                builder.setItems(sequence, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {

                            Intent intent = new Intent(context, ProductDetailsActivity.class);
                            intent.putExtra("pid", cart.getPid());
                            context.startActivity(intent);
                        } else {

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                    ref.child("Cart List").child("User View").child(Prevalent.currentOnlineUser.getNumber())
                                            .child("Products").child(cart.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                Toast.makeText(context, "Product has been removed from cart list", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(context, HomeActivity.class);
                                                context.startActivity(intent);
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
                });

                builder.show();

            }

        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cartProductName, cartProductQuantity, cartProductPrice;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            cartProductName = itemView.findViewById(R.id.cart_name_id);
            cartProductQuantity = itemView.findViewById(R.id.cart_quantity_id);
            cartProductPrice = itemView.findViewById(R.id.cart_price_id);
        }
    }
}
