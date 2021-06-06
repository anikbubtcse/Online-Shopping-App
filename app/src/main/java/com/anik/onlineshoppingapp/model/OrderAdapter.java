package com.anik.onlineshoppingapp.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.onlineshoppingapp.Admin.AdminCategoryActivity;
import com.anik.onlineshoppingapp.Admin.AdminUserProductsActivity;
import com.anik.onlineshoppingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private List<Orders> orders;
    private Context context;

    public OrderAdapter(List<Orders> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view =  inflater.inflate(R.layout.order_item_design,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OrderAdapter.MyViewHolder holder, int position) {


        Orders orderList = orders.get(position);
        holder.nameTv.setText(orderList.getName());
        holder.numberTv.setText(orderList.getNumber());
        holder.totalAmountTv.setText("Total price = "+orderList.getTprice()+" "+"$");
        holder.addressTv.setText(orderList.getAddress());
        holder.cityTv.setText(orderList.getCity());
        holder.dateTv.setText(orderList.getDate());
        holder.timeTv.setText(orderList.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence[] options = new CharSequence[2];
                options[0] = "Yes";
                options[1] = "No";

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Have you shipped the products?");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                                        reference.child("Orders").child(orderList.getNumber()).removeValue();

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                        }
                        else{

                            Intent intent = new Intent(context, AdminCategoryActivity.class);
                            context.startActivity(intent);
                        }

                    }
                });
                builder.show();
            }
        });


        holder.showProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, AdminUserProductsActivity.class);
                intent.putExtra("number",orderList.getNumber());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTv, numberTv, totalAmountTv, addressTv, cityTv, dateTv, timeTv;
        private Button showProductBtn;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.order_name_tv);
            numberTv = itemView.findViewById(R.id.order_number_tv);
            totalAmountTv = itemView.findViewById(R.id.order_total_amount_tv);
            addressTv = itemView.findViewById(R.id.order_address_tv);
            cityTv = itemView.findViewById(R.id.order_city_tv);
            dateTv = itemView.findViewById(R.id.order_date_tv);
            timeTv = itemView.findViewById(R.id.order_time_tv);
            showProductBtn = itemView.findViewById(R.id.order_button_id);
        }
    }
}
