package com.yourapp.desertcraft.model;

public class RateData {
    String id;
    String rating;
    String comments;

    public RateData(String id, String rating, String comments) {
        this.id = id;
        this.rating = rating;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public String getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "RateData{" +
                "id='" + id + '\'' +
                ", rating='" + rating + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}
