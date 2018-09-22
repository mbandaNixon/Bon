package com.example.laptop.bon.checkOut;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laptop.bon.AddShop;
import com.example.laptop.bon.From.LoginActivity;
import com.example.laptop.bon.From.MainActivity;
import com.example.laptop.bon.R;
import com.example.laptop.bon.cart.Main_cart;
import com.example.laptop.bon.cart.Shop2;
import com.example.laptop.bon.cart.cartArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.laptop.bon.cart.Main_cart.FINAL_PRICE;

public class Frag1 extends Fragment {


    ArrayList<Shop2> object;
    TextView cartName, cartPrice, cartQuantity, cartTotalPrice, submitOrder, backHome, priceOne;
    FirebaseAuth mAuth;
    FirebaseUser owner;
    private DatabaseReference mDatabaseOrders;
    View view;
    ProgressBar progressBar;
      String mDatabaseUser;
      Double staticPrice ;
      Double finalP;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
           view = inflater.inflate(R.layout.fragment_frag1, container, false);


        cartName = view.findViewById(R.id.cartName_);
        cartPrice = view.findViewById(R.id.cartPrice);
        cartQuantity = view.findViewById(R.id.cartQuantity);
        cartTotalPrice = view.findViewById(R.id.cartTotalPrice);
        submitOrder = view.findViewById(R.id.submitOrder);
        progressBar = view.findViewById(R.id.progressBarSubmit);
        backHome = view.findViewById(R.id.backHome);
        priceOne = view.findViewById(R.id.priceOne);


        mAuth = FirebaseAuth.getInstance();
        owner = mAuth.getCurrentUser();
         mDatabaseUser  =  FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseOrders = FirebaseDatabase.getInstance().getReference().child("Orders");


        LinearLayout linearLayout = new LinearLayout(getContext());
        TextView DisplayStringArray = new TextView(getActivity());
        linearLayout.addView(DisplayStringArray);
        linearLayout.addView(view);


        final Intent intent = getActivity().getIntent();
      //  Bundle args = intent.getBundleExtra("ORDER_LIST");
        Bundle args2 = intent.getBundleExtra("FINAL_PRICE");
        object = cartArray.getCartListImageUri();




        if ( args2 != null) {
            //object =  args.getParcelableArrayList(Main_cart.ORDER_LIST);
              finalP = (Double) args2.getDouble(FINAL_PRICE);

            assert object != null;
            if (object.size() > 0) {
                try {
                    cartName.append("Name");
                    cartName.append("\n");

                     cartQuantity.append("Quantity");
                    cartQuantity.append("\n");

                    cartPrice.append("Price");
                    cartPrice.append("\n");

                         for (Shop2 s : Objects.requireNonNull(object)) {


                            cartName.append(s.getName());
                            cartName.append("\n");

                            NumberFormat nm = NumberFormat.getNumberInstance();
                            cartQuantity.append(nm.format(s.getQuantity()));
                            cartQuantity.append("\n");

                            cartPrice.append(nm.format(s.getPrice()));
                            cartPrice.append("\n");

                        }
                    view = linearLayout;
                         
                 } catch (Exception e) {
                   Toast.makeText(getActivity(), "Error" + e, Toast.LENGTH_SHORT).show();
                   Log.i("ERROR23", e.toString());
                }

                NumberFormat nm = NumberFormat.getNumberInstance();
                cartTotalPrice.append(nm.format(finalP));
            }
        }

         priceOne.setText("Your kit currently has KSH " + 50000.00);

        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  submitOrderList(object);
            }
        });
        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainIntent = new Intent(getContext(), MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);

            }
        });


        return view;

    }

    private void submitOrderList(ArrayList<Shop2> object) {


        final JSONArray jsonArray_List = new JSONArray();


        for (int i = 0; i < object.size(); i++) {

            jsonArray_List.put(object.get(i).getJSONObject());

        }


        final JSONObject jsonBodyObj = new JSONObject();


        progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference newPost = mDatabaseOrders.push();

        mDatabaseOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                newPost.child("user_id").setValue(mDatabaseUser);
                newPost.child("finalPrice").setValue(finalP);
               newPost.child("order").setValue(jsonArray_List.toString());




                newPost.child("poster").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                           Toast.makeText(getContext(),"Order made Successfully !", Toast.LENGTH_LONG).show();

                            progressBar.setVisibility(View.GONE);

                            cartArray.clearCart();

                            Intent mainIntent = new Intent(getContext(), MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);



                        } else {
                            Toast.makeText(getContext(), "Failed, Try again !", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                         }
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
           Log.i("databaseError", databaseError.toString());
            }
        });

//             jsonBodyObj.put("user_id", owner);
//             jsonBodyObj.put("order_List", jsonArray_List);
//             jsonBodyObj.put("finalPrice", finalP);

        //        showProgress();





    }


}
