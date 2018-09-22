package com.example.laptop.bon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laptop.bon.From.LoginActivity;
import com.example.laptop.bon.cart.Main_cart;
import com.example.laptop.bon.cart.cartArray;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.example.laptop.bon.ViewShop.Shop_id;
import static com.example.laptop.bon.cart.NotificationCountSetClass.setAddToCart;
import static com.example.laptop.bon.cart.NotificationCountSetClass.setNotifyCount;

public class SingleShop extends AppCompatActivity {

    private String post_key = null;
    private DatabaseReference mDtataRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    private ImageView postImage;
    private TextView txtTitle;
    private TextView txtPrice;
    private TextView textDesc, addToCat;

    String post_title,post_price,post_image,post_desc, post_shop, post_uid;
    public static int notificationCountCart = 0;


    String Shop_NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_single_shop);

        Intent intent = getIntent();
        Shop_NAME = intent.getStringExtra("shop_name").trim();
        post_key = getIntent().getExtras().getString("shop_id");


        mDtataRef = FirebaseDatabase.getInstance().getReference().child("Shops").child(Shop_NAME);

        mAuth = FirebaseAuth.getInstance();

        postImage = findViewById(R.id.post_image);
        txtTitle = findViewById(R.id.post_title);
        txtPrice = findViewById(R.id.post_price);
        textDesc = findViewById(R.id.post_desc);
        addToCat = findViewById(R.id.text_action_add2cat);



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent LogInActivity = new Intent( SingleShop.this, LoginActivity.class);
                    LogInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(LogInActivity);
                }
            }
        };


        mDtataRef.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                 post_title = (String) dataSnapshot.child("name").getValue();
                 post_price = (String) dataSnapshot.child("price").getValue();
                 post_image = (String) dataSnapshot.child("picture").getValue();
                 post_desc = (String) dataSnapshot.child("description").getValue();


                txtTitle.setText(post_title);
                txtPrice.setText(post_price);
                textDesc.setText(post_desc);
                Picasso.get().load(post_image).into(postImage);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
             ////////////////////
            }




        });


        addToCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                double doublePrice = Double.parseDouble(post_price);

                if  (cartArray.addCartListImageUri(Shop_NAME, post_key, post_title, doublePrice, post_desc, post_image , 1, doublePrice)){

                    SingleShop.notificationCountCart++;
                    setNotifyCount(SingleShop.notificationCountCart);


                } else {
                    Toast.makeText(getBaseContext(),"Item Already cart.",Toast.LENGTH_SHORT).show();

                }
            }
            }
        );
    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_only, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.cart_only);



        setAddToCart(SingleShop.this, item,notificationCountCart);
        invalidateOptionsMenu();

         return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();



        if (id == R.id.cart_only) {
            startActivity(new Intent(SingleShop.this, Main_cart.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
