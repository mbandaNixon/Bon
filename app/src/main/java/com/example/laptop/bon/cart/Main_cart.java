package com.example.laptop.bon.cart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laptop.bon.R;
import com.example.laptop.bon.SingleShop;
import com.example.laptop.bon.checkOut.CheckOut;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import static com.example.laptop.bon.cart.NotificationCountSetClass.setNotifyCount;

public class Main_cart extends AppCompatActivity {


    static final String NAME_ = "";
    static final String PRICE_ = "";
    public static final String FINAL_PRICE = "";
    public static final String ORDER_LIST = "";


    ArrayList<Shop2> mCartlistImageUri;
    Double orderFinalPrice;


    private Context mContext;
    public TextView Price;
    private TextView Checkout;
    private RecyclerView recyclerView;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cart);


        mContext = Main_cart.this;

        Price = findViewById(R.id.text_total_price);
        Checkout = findViewById(R.id.text_check_out);
        recyclerView = findViewById(R.id.recyclerview_cart);

        Double TotalPrice = cartArray.grandTotal();
        String stringTotal = Double.toString(TotalPrice);
        Price.setText(stringTotal);

        //**** populated products //
        final ArrayList<Shop2> cartlistImageUri = cartArray.getCartListImageUri();

        setCartLayout();   // Buttons to how cart has some product or Empty

        RecyclerView.LayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.setAdapter(new Main_cart.MainCart_RecyclerViewAdapter_clas(recyclerView, cartlistImageUri));


        //CHECK OUT FROM HERE

        Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // OrderListobject = cartArray.getCartListImageUri();
                orderFinalPrice = cartArray.grandTotal();

                Intent intent = new Intent(v.getContext(), CheckOut.class);
               // Bundle bundle = new Bundle();
                Bundle bundle2 = new Bundle();

               // bundle.putParcelableArrayList(ORDER_LIST, OrderListobject);
                bundle2.putDouble(FINAL_PRICE, orderFinalPrice);

               /// intent.putExtra("ORDER_LIST", bundle);
                intent.putExtra("FINAL_PRICE", bundle2);

                v.getContext().startActivity(intent);


            }
        });


        Log.i("Error34", mCartlistImageUri.toString() );
    }

    public void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }

    public class MainCart_RecyclerViewAdapter_clas extends RecyclerView.Adapter<Main_cart.MainCart_RecyclerViewAdapter_clas.ViewHolder> {

        private int minteger = 0;

      //  private ArrayList<Shop2> mCartlistImageUri;
        private RecyclerView mRecyclerView;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final TextView p_price, p_name, p_desc;
            public final LinearLayout mLayoutItem, mLayoutRemove ;

            LinearLayout increase, decrease;
            TextView displayInteger ;



            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = view.findViewById(R.id.image_cartlist);
                mLayoutItem = view.findViewById(R.id.layout_item_description);
                mLayoutRemove = view.findViewById(R.id.layout__remove_action);

                p_name = view.findViewById(R.id.p_name);
                p_price = view.findViewById(R.id.p_price);
                p_desc = view.findViewById(R.id.p_desc);


                increase = view.findViewById(R.id.increase_p);
                decrease = view.findViewById(R.id.decrease_p);
                displayInteger = view.findViewById(R.id.integer_number);

            }
        }

        public MainCart_RecyclerViewAdapter_clas(RecyclerView recyclerView, ArrayList<Shop2> wishlistImageUri) {
            mRecyclerView = recyclerView;
            mCartlistImageUri = wishlistImageUri;
        }

        @Override
        public Main_cart.MainCart_RecyclerViewAdapter_clas.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cartlist_item, parent, false);
            return new Main_cart.MainCart_RecyclerViewAdapter_clas.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(Main_cart.MainCart_RecyclerViewAdapter_clas.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
             }
        }

        @Override
        public void onBindViewHolder(final Main_cart.MainCart_RecyclerViewAdapter_clas.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {


             final Shop2 developersList = mCartlistImageUri.get(position);

            holder.mImageView.setImageURI(developersList.getPicture());
            holder.p_name.setText(developersList.getName());
            //price
            final Double prc = developersList.getPrice();
            String stringTotal = Double.toString(prc);
            holder.p_price.setText(stringTotal);

            holder.p_desc.setText(developersList.getDecription());
            holder.displayInteger.setText(Integer.toString(developersList.getQuantity()));



            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            //Set click action
            holder.mLayoutRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartArray imageUrlUtils = new cartArray();
                    imageUrlUtils.removeCartListImageUri(position);
                    notifyDataSetChanged();

                    //Decrease notification count
                    SingleShop.notificationCountCart--;
                     setNotifyCount(SingleShop.notificationCountCart);

                    refresh();


                }


            });


            holder.increase.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    minteger = developersList.getQuantity();
                    if(minteger == 10){
                        return;
                    }
                    minteger = minteger + 1;
                    holder.displayInteger.setText(Integer.toString(minteger));

                    //calculate price
                     cartArray.addCartListImageUri2(position, developersList.getShopname(), developersList.getUser_id(), developersList.getName(),
                            developersList.getPrice(), developersList.getDecription(), developersList.getPicture(), minteger);
                    notifyDataSetChanged();

                    refresh();

                }
            });



            holder.decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    minteger = developersList.getQuantity();
//
                    if(minteger == 1){
                        return;
                    }
                    minteger = minteger - 1;
                    holder.displayInteger.setText(Integer.toString(minteger));

                   // user_id, name, price, decription, picture, quantity, finalPrice

                    cartArray.addCartListImageUri2(position, developersList.getShopname(), developersList.getUser_id(), developersList.getName(),
                            developersList.getPrice(), developersList.getDecription(), developersList.getPicture(), minteger);
                    notifyDataSetChanged();

                    refresh();


                }

            });

        }




        @Override
        public int getItemCount() {
            return mCartlistImageUri.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


    }

    private void setCartLayout() {
        LinearLayout layoutCartItems = findViewById(R.id.layout_cart_items);
        LinearLayout layoutCartPayments = findViewById(R.id.layout_cart_payment);
        LinearLayout layoutCartNoItems = findViewById(R.id.layout_cart_empty);

        if(SingleShop.notificationCountCart >0){
            layoutCartNoItems.setVisibility(View.GONE);
            layoutCartItems.setVisibility(View.VISIBLE);
            layoutCartPayments.setVisibility(View.VISIBLE);
        }else {
            layoutCartNoItems.setVisibility(View.VISIBLE);
            layoutCartItems.setVisibility(View.GONE);
            layoutCartPayments.setVisibility(View.GONE);

            Button bStartShopping = findViewById(R.id.bAddNew);
            bStartShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
}
