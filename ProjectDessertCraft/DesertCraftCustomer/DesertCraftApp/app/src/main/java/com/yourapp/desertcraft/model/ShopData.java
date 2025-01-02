package com.yourapp.desertcraft.model;

public class ShopData {
    String name;
    String location;
    String image;

    public ShopData(String name, String location, String image) {
        this.name = name;
        this.location = location;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "ShopData{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

