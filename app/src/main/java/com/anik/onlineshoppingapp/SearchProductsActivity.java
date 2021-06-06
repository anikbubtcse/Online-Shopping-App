package com.anik.onlineshoppingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.anik.onlineshoppingapp.model.MyAdapter;
import com.anik.onlineshoppingapp.model.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SearchProductsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DatabaseReference reference;
    private MyAdapter adapter;
    private List<Products> productsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        toolbar = findViewById(R.id.search_toolbar);
        toolbar.setTitle("Search");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        reference = FirebaseDatabase.getInstance().getReference().child("Product Information");

        Init();
        showAllProducts();
    }

    private void showAllProducts() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot ds : snapshot.getChildren()) {

                        Products product = ds.getValue(Products.class);
                        productsList.add(product);

                    }

                    adapter = new MyAdapter(productsList, SearchProductsActivity.this);
                    recyclerView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setBackgroundColor(getResources().getColor(android.R.color.white));
        searchView.setOnQueryTextListener(this);
        return true;

    }


    private void Init() {

        recyclerView = findViewById(R.id.search_recyclerview);
        productsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String data = newText.toLowerCase();
        List<Products> newList = new ArrayList<>();

        for (Products product : productsList) {

            if (product.getName().toLowerCase().contains(data)) {

                newList.add(product);
            }

        }

        adapter.updateList(newList);
        return true;
    }
}