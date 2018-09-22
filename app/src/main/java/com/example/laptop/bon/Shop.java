package com.example.laptop.bon;

public class Shop {

    private String name;
    private String price;
    private String description;
    private String picture;

    public Shop(){}

    public Shop(String name, String price, String description){
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    //***************************


    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }
}
