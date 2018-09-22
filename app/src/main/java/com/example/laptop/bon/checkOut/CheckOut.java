package com.example.laptop.bon.checkOut;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laptop.bon.R;
import com.example.laptop.bon.cart.Main_cart;
import com.example.laptop.bon.cart.Shop2;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.laptop.bon.cart.Main_cart.FINAL_PRICE;

public class CheckOut extends AppCompatActivity {

     Frag1 fragment1;
     ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        getSupportActionBar().setTitle("Checkout Page");


        fragment1 = new Frag1();



        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {
        CheckOut.ViewPagerAdapter_class adapter = new CheckOut.ViewPagerAdapter_class(getSupportFragmentManager());


        adapter.addFrag(new Frag1(), "Payment");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter_class extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter_class(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }



        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }

    }


}
