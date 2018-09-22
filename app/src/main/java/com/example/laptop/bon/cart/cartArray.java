package com.example.laptop.bon.cart;

import java.util.ArrayList;

import static com.google.android.gms.common.util.ArrayUtils.contains;

public class cartArray {


    private static ArrayList<Shop2> cartListImageUri = new ArrayList<>();

    // price
    public static Double grandTotal(){

        Double totalPrice = 0.00;

        for(int i = 0 ; i < cartListImageUri.size(); i++) {
            totalPrice += cartListImageUri.get(i).getFinalPrice();
        }

        return totalPrice;
    }


    public static boolean addCartListImageUri(String shopname,String user_id, String name, Double price, String decription, String picture,
                                               int quantity, Double finalPrice) {
         //boolean success = false;
        if (!contains(cartListImageUri, picture)) {

            Shop2 developers = new Shop2(shopname, user_id, name, price, decription, picture, quantity, finalPrice);
            cartListImageUri.add(developers);

            return true;
        } else {
             return false;
        }

    }

    private static boolean contains(ArrayList <Shop2> list, String img) {
        for (Shop2 item : list) {
            if (item.getPicture().equals(img)) {
                return true;
            }
        }
        return false;
    }


    public void removeCartListImageUri(int position) { cartListImageUri.remove(position); }
    public static ArrayList<Shop2> getCartListImageUri(){ return cartListImageUri; }
    public static void clearCart() { cartListImageUri.clear(); }



    public static void addCartListImageUri2(int position, String shopName, String user_id, String name, Double price, String decription, String picture,
                                           int quantity) {


        if (contains(cartListImageUri, picture)) {
            Double totalPerItem = quantity * price;

            Shop2 developers =  new Shop2(shopName, user_id, name, price, decription, picture, quantity, totalPerItem );
            cartListImageUri.set(position, developers);

        }

    }


}
