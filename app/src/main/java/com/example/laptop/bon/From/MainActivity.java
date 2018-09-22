package com.example.laptop.bon.From;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laptop.bon.AddShop;
import com.example.laptop.bon.R;
import com.example.laptop.bon.ShoptoAdd;
import com.example.laptop.bon.ViewShop;
import com.facebook.drawee.view.DraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUsername;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    String currentUserID;

    private DraweeView CategoryOne, CategoryTwo, CategoryThree, CategoryFour;
    private TextView cat1, cat2, cat3, cat4;
    private String cat1_Title, cat2_Title, cat3_Title, cat4_Title;
    Toolbar toolBar;

    public static String shopName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);



        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Home");



        CategoryOne = findViewById(R.id.categoryOne);
        CategoryTwo = findViewById(R.id.categoryTwo);
        CategoryThree = findViewById(R.id.categoryThree);
        CategoryFour = findViewById(R.id.categoryFour);

        cat1 = findViewById(R.id.category1);
        cat2= findViewById(R.id.category2);
        cat3 = findViewById(R.id.category3);
        cat4 = findViewById(R.id.category4);

        cat1_Title = cat1.getText().toString().trim();
        cat2_Title = cat2.getText().toString().trim();
        cat3_Title = cat3.getText().toString().trim();
        cat4_Title = cat4.getText().toString().trim();



        CategoryOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoForBrands(cat1_Title);
            }
        });

        CategoryTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoForBrands(cat2_Title);
            }
        });

        CategoryThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoForBrands(cat3_Title);
            }
        });

        CategoryFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoForBrands(cat4_Title);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");




        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = navView.findViewById(R.id.nav_profile_image);
        NavProfileUsername = navView.findViewById(R.id.nav_user_fullname);

        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {

                    if (dataSnapshot.hasChild("fullname"))
                    {
                        String fullname = dataSnapshot.child("fullname").getValue().toString();
                        NavProfileUsername.setText(fullname);
                    }

                    if (dataSnapshot.hasChild("image"))
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                UserMenuSelector (item);
                return false;
            }
        });


    }

    private void GoForBrands(String cat_title) {
        Intent skipIntent = new Intent(getApplicationContext(), ViewShop.class);
        skipIntent.putExtra(shopName, cat_title);
        startActivity(skipIntent);
    }

    private void SendUserToShopActivity() {
        Intent addNewShopIntent = new Intent(MainActivity.this, AddShop.class);
        startActivity(addNewShopIntent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)                        //not authenticated
        {
            SendUserToLoginActivity();                  //User is sent to the Login Activity
        }
        else
            {
                CheckUserExistence();
            }
    }

    private void CheckUserExistence()
    {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.hasChild(current_user_id))        //User is authenticated but has no record on the real time database
                {
                    SendUserToSetupActivity();
                }                                                   //send user to the setup activity
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void UserMenuSelector(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.nav_shop:
                SendUserToShopActivity();
                break;
            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_inbox:
                Toast.makeText(this, "Inbox", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_notifications:
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_my_gigs:
                Toast.makeText(this, "My Gigs", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_view_as_buyer:
                Toast.makeText(this, "View As Buyer", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_one, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.addNew) {
            startActivity(new Intent(this, ShoptoAdd.class));

        }

        if(item.getItemId() == R.id.LogOut) {
            FirebaseAuth.getInstance().signOut();
            Intent LogInActivity = new Intent( MainActivity.this, LoginActivity.class);
             LogInActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(LogInActivity);
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
