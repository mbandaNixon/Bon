package com.example.laptop.bon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ShoptoAdd extends AppCompatActivity {

    private TextView catOne, catTwo, catThree, catFour;
    public static final String shopType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        catOne = findViewById(R.id.catOne);
        catTwo = findViewById(R.id.catTwo);
        catThree = findViewById(R.id.catThree);
        catFour = findViewById(R.id.catFour);


        final String shop1 = catOne.getText().toString();
        final String shop2 = catTwo.getText().toString();
        final String shop3 = catThree.getText().toString();
        final String shop4 = catFour.getText().toString();

        catOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewBrand(shop1);
            }
        });

        catTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewBrand(shop2);
            }
        });


        catThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewBrand(shop3);
            }
        });

        catFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewBrand(shop4);
            }
        });
    }

    private void AddNewBrand(String Shop) {
         Intent skipIntent = new Intent(getApplicationContext(), AddShop.class);
        skipIntent.putExtra(shopType, Shop);
        startActivity(skipIntent);
    }
}
