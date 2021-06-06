package com.anik.onlineshoppingapp.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.onlineshoppingapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private List<UserCart> products;

    public ProductAdapter(List<UserCart> products) {
        this.products = products;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_item_design,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductAdapter.MyViewHolder holder, int position) {

        UserCart userCart = products.get(position);
        holder.nameTv.setText(userCart.getPname());
        holder.quantityTv.setText(userCart.getQuantity());
        holder.priceTv.setText(userCart.getPrice());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTv,quantityTv,priceTv;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.cart_name_id);
            quantityTv = itemView.findViewById(R.id.cart_quantity_id);
            priceTv = itemView.findViewById(R.id.cart_price_id);
        }
    }
}
