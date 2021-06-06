package com.anik.onlineshoppingapp.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.onlineshoppingapp.Admin.AdminMaintainProductsActivity;
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
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Products> productList;
    private Context context;

    public MyAdapter() {
    }

    public MyAdapter(List<Products> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.product_item_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyAdapter.MyViewHolder holder, int position) {

        Products products = productList.get(position);
        holder.productName.setText(products.getName());
        holder.productDescription.setText(products.getDescription());
        holder.productPrice.setText("Price = " + products.getPrice() + "$");
        Picasso.get().load(Uri.parse(products.getImage())).into(holder.productImageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String databaseParent = Paper.book().read(Prevalent.databaseParentKey);
                if (databaseParent.equals("Users")) {

                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    intent.putExtra("pid", products.getPid());
                    context.startActivity(intent);

                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    CharSequence[] options = new CharSequence[2];
                    options[0] = "Edit";
                    options[1] = "Remove";
                    builder.setTitle("Please select an options ...");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (i == 0) {

                                Intent intent = new Intent(context, AdminMaintainProductsActivity.class);
                                intent.putExtra("pid", products.getPid());
                                context.startActivity(intent);

                            } else if (i == 1) {

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                                        ref.child("Product Information").child(products.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(context, "Product has successfully been removed", Toast.LENGTH_LONG).show();
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

            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, productPrice, productDescription;
        private ImageView productImageView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productDescription = itemView.findViewById(R.id.product_description);
            productImageView = itemView.findViewById(R.id.product_image);
        }

    }

    public void updateList(List<Products> newList) {

        productList = newList;
        notifyDataSetChanged();
    }
}
