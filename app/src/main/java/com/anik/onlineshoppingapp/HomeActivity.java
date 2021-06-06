package com.anik.onlineshoppingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anik.onlineshoppingapp.model.MyAdapter;
import com.anik.onlineshoppingapp.model.Products;
import com.anik.onlineshoppingapp.model.Users;
import com.anik.onlineshoppingapp.prevalent.Prevalent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    List<Products> productList;
    DatabaseReference reference;
    private Products product;
    private MyAdapter adapter;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Product Information");
        getFirebaseData();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String databaseParent = Paper.book().read(Prevalent.databaseParentKey);
                if (databaseParent.equals("Users")) {

                    Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                    startActivity(intent);
                }

            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView userPhotoImageView = headerView.findViewById(R.id.user_profile_image);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                String databaseParent = Paper.book().read(Prevalent.databaseParentKey);

                if (databaseParent.equals("Users")) {

                    Users user = snapshot.child("Users").child(Prevalent.currentOnlineUser.getNumber()).getValue(Users.class);
                    Prevalent.currentOnlineUser = user;
                    userNameTextView.setText(Prevalent.currentOnlineUser.getName());
                    Picasso.get().load(Prevalent.currentOnlineUser.getImage()).into(userPhotoImageView);

                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        onClick(navigationView);
    }

    private void getFirebaseData() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {

                    product = ds.getValue(Products.class);
                    productList.add(product);
                }
                adapter = new MyAdapter(productList, HomeActivity.this);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void onClick(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_cart) {

                    String databaseParent = Paper.book().read(Prevalent.databaseParentKey);

                    if (databaseParent.equals("Users")) {
                        Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                        startActivity(intent);
                    }

                } else if (id == R.id.nav_search) {

                    String databaseParent = Paper.book().read(Prevalent.databaseParentKey);

                    if (databaseParent.equals("Users")) {

                        Intent intent = new Intent(getApplicationContext(), SearchProductsActivity.class);
                        startActivity(intent);

                    }

                } else if (id == R.id.nav_categories) {

                    String databaseParent = Paper.book().read(Prevalent.databaseParentKey);

                    if (databaseParent.equals("Users")) {


                    }

                } else if (id == R.id.nav_settings) {

                    String databaseParent = Paper.book().read(Prevalent.databaseParentKey);

                    if (databaseParent.equals("Users")) {

                        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(intent);

                    }


                } else if (id == R.id.nav_logout) {

                    String databaseParent = Paper.book().read(Prevalent.databaseParentKey);

                    if (databaseParent.equals("Users")) {

                        Paper.book().destroy();

                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


}