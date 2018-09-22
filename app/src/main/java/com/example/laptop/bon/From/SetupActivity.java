package com.example.laptop.bon.From;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.laptop.bon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity
{
    private EditText UserName, FullName, PhoneNumber;
    private Button SaveInformation;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseUsers;
     private StorageReference mStorageRefence;

    String currentUserID;
    private Uri mImageUrl = null;
    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mStorageRefence = FirebaseStorage.getInstance().getReference().child("Users_image");

        UserName = findViewById(R.id.setup_username);
        FullName = findViewById(R.id.setup_fullname);
        PhoneNumber = findViewById(R.id.setup_phone_number);
        ProfileImage = findViewById(R.id.setup_profile_image);


        SaveInformation = findViewById(R.id.setup_save_button);
        loadingBar = new ProgressDialog(this);

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galaryIntent = new Intent(Intent.ACTION_PICK);
                galaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galaryIntent.setType("image/*");
                startActivityForResult(galaryIntent, GALLERY_REQUEST);


            }
        });

        SaveInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
          /*  Uri uri = data.getData();


            // start picker to get image for cropping and then use the image in cropping activity
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);   */
            //upload photo from galary
                 mImageUrl = data.getData();

                ProfileImage.setImageURI(mImageUrl);



        }




       /* if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUrl = result.getUri();
                ProfileImage.setImageURI(mImageUrl);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SetupActivity.this, "Failed", Toast.LENGTH_LONG).show();

            }
        } */
    }

    private void SaveAccountSetupInformation() {
        final String username = UserName.getText().toString();
        final String fullname = FullName.getText().toString();
        final String phone_number = PhoneNumber.getText().toString();

        if(!TextUtils.isEmpty(username) &&  !TextUtils.isEmpty(fullname) && !TextUtils.isEmpty(phone_number) && mImageUrl != null){


            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait... Account Creation in Progress");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            final StorageReference filePath = mStorageRefence.child(mImageUrl.getLastPathSegment());

            filePath.putFile(mImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final String user_id = mAuth.getCurrentUser().getUid();


                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {
                                final Uri downloadUrl = task.getResult();

//                                mDatabaseUsers.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//


                                Log.i("UrlImage", downloadUrl.toString());

                                mDatabaseUsers.child(user_id).child("name").setValue(username);
                                        mDatabaseUsers.child(user_id).child("fullname").setValue(fullname);
                                        mDatabaseUsers.child(user_id).child("phone_number").setValue(phone_number);
                                        mDatabaseUsers.child(user_id).child("image").setValue(downloadUrl.toString());

                                        Toast.makeText(SetupActivity.this, "Profile set success", Toast.LENGTH_LONG).show();


                                        loadingBar.dismiss();


                                        Intent setUpIntent = new Intent(SetupActivity.this, MainActivity.class);
                                        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(setUpIntent);


//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//                                        loadingBar.dismiss();
//
//                                    }
//                                });

                            }

                        }
                    });
                }
            });

        } else {

            loadingBar.dismiss();
            Toast.makeText(SetupActivity.this, "Some Field missing", Toast.LENGTH_LONG).show();

        }

    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();               //User not allowed to come back to the login activity unless by clicking on the logout button on the main activity
    }
}