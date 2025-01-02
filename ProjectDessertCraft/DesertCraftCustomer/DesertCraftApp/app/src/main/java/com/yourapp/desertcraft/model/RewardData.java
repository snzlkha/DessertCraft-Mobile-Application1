package com.yourapp.desertcraft.model;

public class RewardData {
    String id;
    String points;

    public RewardData(String id, String points) {
        this.id = id;
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public String getPoints() {
        return points;
    }

    @Override
    public String toString() {
        return "RewardData{" +
                "id='" + id + '\'' +
                ", points='" + points + '\'' +
                '}';
    }
}

