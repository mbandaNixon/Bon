package com.example.laptop.bon.cart;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Shop2 implements Parcelable {



    String user_id;
      String name;
      Double price;
      String decription;
      String picture;
    Double finalPrice;
    int quantity;
    String shopname;



    Shop2( String shopname, String user_id, String name, Double price, String decription, String picture, int quantity) {
        this.user_id = user_id;
        this.name = name;
        this.price = price;
        this.decription = decription;
        this.picture = picture;
        this.quantity = quantity;
        this.shopname = shopname;
    }
    //shopname, user_id, name, price, decription, picture, quantity, finalPrice

    Shop2( String shopname, String user_id, String name, Double price, String decription, String picture, int quantity, Double finalPrice){
        this.user_id = user_id;
         this.name = name;
        this.price = price;
        this.decription = decription;
        this.picture = picture;
        this.quantity = quantity;
        this.finalPrice = finalPrice;
        this.shopname = shopname;
    }

    protected Shop2(Parcel in) {
        user_id = in.readString();
        name = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        decription = in.readString();
        picture = in.readString();
        if (in.readByte() == 0) {
            finalPrice = null;
        } else {
            finalPrice = in.readDouble();
        }
        quantity = in.readInt();
        shopname = in.readString();
    }

    public static final Creator<Shop2> CREATOR = new Creator<Shop2>() {
        @Override
        public Shop2 createFromParcel(Parcel in) {
            return new Shop2(in);
        }

        @Override
        public Shop2[] newArray(int size) {
            return new Shop2[size];
        }
    };

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    ////////////////////////////////


    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getDecription() {
        return decription;
    }

    public String getPicture() {
        return picture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(name);
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }
        dest.writeString(decription);
        dest.writeString(picture);
        if (finalPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(finalPrice);
        }
        dest.writeInt(quantity);
        dest.writeString(shopname);
    }


    public JSONObject getJSONObject() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("price", price);
            obj.put("description", decription);
            obj.put("picture", picture);
            obj.put("pricePerItem", price);
            obj.put("quantity", quantity);
            obj.put("shopname", shopname);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
