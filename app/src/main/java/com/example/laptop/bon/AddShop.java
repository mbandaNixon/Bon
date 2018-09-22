package com.example.laptop.bon;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddShop extends AppCompatActivity {


    private ImageButton imgbtn;
    private EditText brandNmae, brandPrice, brandDesc;
    private TextView sendBrand;


    private Uri mImageUri = null;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private static final int GALLERY_REQUEST = 1;

    //private LinearLayout mLinearLayout;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUsers;

    private ProgressBar progressBar;
     String Shop_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_brand);

        Intent intent = getIntent();
         Shop_id = intent.getStringExtra(ShoptoAdd.shopType);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mStorage = FirebaseStorage.getInstance().getReference();
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Shops");
        mDatabase = FirebaseDatabase.getInstance().getReference();


        imgbtn = findViewById(R.id.pickImage);
        brandNmae = findViewById(R.id.brandName);
        brandPrice = findViewById(R.id.brandPrice);
        brandDesc = findViewById(R.id.brandDesc);

        sendBrand = findViewById(R.id.brandSubmit);
        progressBar = findViewById(R.id.progressBar);



        imgbtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST);

            }
        });

        sendBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_data();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //upload photo from galary
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();

            imgbtn.setImageURI(mImageUri);

        }
    }



    private void submit_data() {

        final String name = brandNmae.getText().toString();
        final String price = brandPrice.getText().toString();
        final String desc = brandDesc.getText().toString();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(price) && !TextUtils.isEmpty(desc) && mImageUri!= null ) {

            progressBar.setVisibility(View.VISIBLE);
            sendBrand.setVisibility(View.GONE);

            final StorageReference filePath = mStorage.child("Brands_Photos").child(mImageUri.getLastPathSegment());  //child created in Storage foleder in FB
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    final DatabaseReference newPost = mDatabase.child("Shops").child(Shop_id).push();


                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {


                            if (task.isSuccessful()) {
                                final Uri downloadUrl = task.getResult();

                                 mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        newPost.child("name").setValue(name);
                                        newPost.child("price").setValue(price);
                                        newPost.child("description").setValue(desc);


                                        newPost.child("picture").setValue(downloadUrl.toString());
                                        newPost.child("uid").setValue(mCurrentUser.getUid());
                                        newPost.child("poster").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()) {

                                                    Snackbar snackbar = Snackbar.make(findViewById(R.id.snackAddBrand), "Shop Add Success !",
                                                            Snackbar.LENGTH_LONG);snackbar.show();

                                                    snackbar.show();

                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    sendBrand.setVisibility(View.VISIBLE);



                                                } else {
                                                    Toast.makeText(AddShop.this, "Image Url retrieving failed", Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    sendBrand.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }
                    });
                    //88888888888888888888










                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddShop.this, "Failed", Toast.LENGTH_LONG).show();


                }
            });

        } else {
            Toast.makeText(AddShop.this, "Fill all fields", Toast.LENGTH_LONG).show();}
    }
}
