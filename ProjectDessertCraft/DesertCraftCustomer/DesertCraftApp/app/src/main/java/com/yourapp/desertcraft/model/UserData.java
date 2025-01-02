package com.yourapp.desertcraft.model;

public class UserData {
    String uid;
    String name;
    String email;
    String dob;
    String gender;
    String mobile;
    String image;
    String created_on;
    String updated_on;

    public UserData(String uid, String name, String email, String dob, String gender, String mobile, String image, String created_on, String updated_on) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.gender = gender;
        this.mobile = mobile;
        this.image = image;
        this.created_on = created_on;
        this.updated_on = updated_on;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getMobile() {
        return mobile;
    }

    public String getImage() {
        return image;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dob='" + dob + '\'' +
                ", gender='" + gender + '\'' +
                ", mobile='" + mobile + '\'' +
                ", image='" + image + '\'' +
                ", created_on='" + created_on + '\'' +
                ", updated_on='" + updated_on + '\'' +
                '}';
    }
}
