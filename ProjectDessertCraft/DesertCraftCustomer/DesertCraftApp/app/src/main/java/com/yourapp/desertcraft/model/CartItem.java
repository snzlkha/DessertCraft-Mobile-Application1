package com.yourapp.desertcraft.model;

public class CartItem {
    private int id;
    private String uid;
    private String did;
    private String name;
    private String desc;
    private String image;
    private String customization;
    private String date;
    private double price;
    private int quantity;
    private String createdOn;

    public CartItem(int id, String uid, String did, String name, String desc, String image, String customization, String date, double price, int quantity, String createdOn) {
        this.id = id;
        this.uid = uid;
        this.did = did;
        this.name = name;
        this.desc = desc;
        this.image = image;
        this.customization = customization;
        this.date = date;
        this.price = price;
        this.quantity = quantity;
        this.createdOn = createdOn;
    }

    public int getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getDid() {
        return did;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getCustomization() {
        return customization;
    }

    public String getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", did='" + did + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", image='" + image + '\'' +
                ", customization='" + customization + '\'' +
                ", date='" + date + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", createdOn='" + createdOn + '\'' +
                '}';
    }
}
