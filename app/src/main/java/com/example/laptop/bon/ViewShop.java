package com.example.laptop.bon;

import android.content.Context;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laptop.bon.From.LoginActivity;
import com.example.laptop.bon.From.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewShop extends AppCompatActivity {

     private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseUsers;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase database;
    List<Shop> list;
    RecyclerView recycle;

    public static String Shop_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shop);

        Intent intent = getIntent();
        Shop_id = intent.getStringExtra(MainActivity.shopName);
        getSupportActionBar().setTitle(Shop_id);


         mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Shops" + "/" + Shop_id);
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        database = FirebaseDatabase.getInstance();
        checkLogin();

//        mDatabaseUsers.keepSynced(true);
//        mDatabaseRef.keepSynced(true);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {
                    Intent LogInActivity = new Intent( ViewShop.this, LoginActivity.class);
                    LogInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(LogInActivity);
                }
            }
        };

        recycle = findViewById(R.id._list);
       recycle.setLayoutManager(new LinearLayoutManager(this));
//        RecyclerView.LayoutManager recyce = new GridLayoutManager(ViewShop.this,2);
//        recycle.setLayoutManager(recyce);
//        recycle.setItemAnimator( new DefaultItemAnimator());
//         recycle.setHasFixedSize(true);
//        recycle.setLayoutManager(new LinearLayoutManager(this));




    }


    @Override
    protected void onStart() {
        super.onStart();


        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Shop, ViewHolder2> firebaseRecyclerAdapter = new
                    FirebaseRecyclerAdapter<Shop, ViewHolder2>(
                            Shop.class,
                            R.layout.single_shop,
                            ViewHolder2.class,
                            mDatabaseRef

                    ) {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        protected void populateViewHolder(ViewHolder2 viewHolder, Shop model, int position) {

                           final String post_key = getRef(position).getKey();

                            viewHolder.setTitle(model.getName());
                            viewHolder.setPrice(model.getPrice());
                             viewHolder.setDesc(model.getDescription());
                            viewHolder.setImageUrl(getApplicationContext(), model.getPicture());

                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent singleBlogIntent = new Intent(ViewShop.this, SingleShop.class);
                                    singleBlogIntent.putExtra("shop_name", Shop_id);
                                    singleBlogIntent.putExtra("shop_id", post_key);
                                    startActivity(singleBlogIntent);


                                }
                            });



                        }

                    };

            //set adapter to recyclerview
            recycle.setAdapter(firebaseRecyclerAdapter);
        }



    private void checkLogin() {

        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent setUpIntent = new Intent(ViewShop.this, LoginActivity.class);
                        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setUpIntent);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }




    public static class ViewHolder2 extends RecyclerView.ViewHolder {

        View mView;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }


        void setTitle(String title) {
            TextView post_title = mView.findViewById(R.id.catName_);
            post_title.setText(title);

        }

        void setPrice(String post) {
            TextView post_text = mView.findViewById(R.id.catPrice_);
            post_text.setText(post);

        }

        void setDesc(String post) {
            TextView post_username = mView.findViewById(R.id.catDesc_);
            post_username.setText(post);

        }



        public void setImageUrl(Context context, String imageUrl){
            ImageView post_image = mView.findViewById(R.id.catPic_);
            Picasso.get().load(imageUrl).into(post_image);
        }
    }
}
