package com.yourapp.desertcraft.model;

public class BannerData {
    String id;
    String image;

    public BannerData(String id, String image) {
        this.id = id;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "BannerData{" +
                "id='" + id + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

