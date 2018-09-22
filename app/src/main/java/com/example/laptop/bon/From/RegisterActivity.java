package com.example.laptop.bon.From;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.laptop.bon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity
{
    private EditText UserEmail, UserPassword, UserConfrirmPassword;
    private Button CreateAccountButton;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null)                        //User is already registered
//        {
//            SendUserToMainActivity();                  //User is sent to the Main Activity
//        }
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserConfrirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        CreateAccountButton = (Button) findViewById(R.id.register_create_account);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateNewAccount();
            }
        });

    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();               //User not allowed to come back to the login activity unless by clicking on the logout button on the main activity
    }

    private void CreateNewAccount()
    {
        final String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfrirmPassword.getText().toString();

        if (!password.equals(confirmPassword)) {

            Toast.makeText(RegisterActivity.this, "Passwords Don't match !", Toast.LENGTH_LONG).show();


        } else if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)  ){
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please Wait... Account Creation in Progress");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()) {

                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_id = mDatabase.child(user_id);

                        current_user_id.child("email").setValue(email);
                        current_user_id.child("image").setValue("default");

                        loadingBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration Success", Toast.LENGTH_LONG).show();

                        SendUserToSetupActivity();

                    }
                }
            });
        } else {
            loadingBar.dismiss();
            Toast.makeText(RegisterActivity.this, "All Fields Required", Toast.LENGTH_LONG).show();

        }
    }

    private void SendUserToSetupActivity()
    {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
