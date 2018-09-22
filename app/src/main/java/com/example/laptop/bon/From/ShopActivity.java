package com.example.laptop.bon.From;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.laptop.bon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ShopActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private ImageButton SelectShopImage;
    private Button UpdateShopButton;
    private EditText ShopDescription;

    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String Description;

    private StorageReference ShopImagesReference;
    private DatabaseReference UsersRef, ShopsRef;
    private FirebaseAuth mAuth;

    private String saveCurrentDate, saveCurrentTime, shopRandomName, downloadUrl, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        ShopImagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ShopsRef = FirebaseDatabase.getInstance().getReference().child("Shops");

        SelectShopImage = findViewById(R.id.select_shop_image);
        UpdateShopButton = findViewById(R.id.update_shop_button);
        ShopDescription = findViewById(R.id.shop_description);
        loadingBar = new ProgressDialog(this);

        mToolbar = findViewById(R.id.update_shop_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Shop");

        SelectShopImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OpenGallery();
            }
        });


        UpdateShopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ValidateShopInfo();
            }
        });
    }

    private void ValidateShopInfo()
    {
        Description = ShopDescription.getText().toString();

        if (ImageUri == null)
        {
            Toast.makeText(this, "Please select shop image...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please describe your shop...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Add New Shop");
            loadingBar.setMessage("Please Wait as we are updating your new shop...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            storingImageToFirebaseStorage();
        }
    }

    private void storingImageToFirebaseStorage()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        shopRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = ShopImagesReference.child("Shop Images").child(ImageUri.getLastPathSegment() + shopRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    downloadUrl = task.getResult().getStorage().getDownloadUrl().toString();
                    Toast.makeText(ShopActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                    SavingShopInformationToDatabase();

                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(ShopActivity.this, "Error occurred: "+ message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavingShopInformationToDatabase()
    {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String userFullName = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage =dataSnapshot.child("profileimage").getValue().toString();


                    HashMap shopsMap = new HashMap();
                        shopsMap.put("uid", current_user_id);
                        shopsMap.put("date", saveCurrentDate);
                        shopsMap.put("time", saveCurrentTime);
                        shopsMap.put("description", Description);
                        shopsMap.put("shopimage", downloadUrl);
                        shopsMap.put("profileimage", userProfileImage);
                        shopsMap.put("fullname", userFullName);
                    ShopsRef.child(current_user_id + shopRandomName).updateChildren(shopsMap)
                            .addOnCompleteListener(new OnCompleteListener()
                            {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        SendUserToMainActivity();

                                        Toast.makeText(ShopActivity.this, "New Shop Updated Successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else 
                                    {
                                        Toast.makeText(ShopActivity.this, "Error Occurred While Updating Your Shop", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            SelectShopImage.setImageURI(ImageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ShopActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
