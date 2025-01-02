package com.yourapp.desertcraft.model;

public class OrderData {
    int id;
    int desert_id;
    String uid;
    String image;
    String desert_name;
    String description;
    String customization;
    String price;
    String quantity;
    String on_date;
    String instruction;
    String design;
    String add_on_price;
    int order_status;
    int payment_status;
    int tracking_status;
    String created_on;
    String rating;
    String comments;

    public OrderData(int id, int desert_id, String uid, String image, String desert_name, String description, String customization, String price, String quantity, String on_date, String instruction, int order_status, int payment_status, int tracking_status, String created_on, String rating, String comments) {
        this.id = id;
        this.desert_id = desert_id;
        this.uid = uid;
        this.image = image;
        this.desert_name = desert_name;
        this.description = description;
        this.customization = customization;
        this.price = price;
        this.quantity = quantity;
        this.on_date = on_date;
        this.instruction = instruction;
        this.order_status = order_status;
        this.payment_status = payment_status;
        this.tracking_status = tracking_status;
        this.created_on = created_on;
        this.rating = rating;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public int getDesert_id() {
        return desert_id;
    }

    public String getUid() {
        return uid;
    }

    public String getImage() {
        return image;
    }

    public String getDesert_name() {
        return desert_name;
    }

    public String getDescription() {
        return description;
    }

    public String getCustomization() {
        return customization;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getOn_date() {
        return on_date;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getDesign() {
        return design;
    }

    public String getAdd_on_price() {
        return add_on_price;
    }

    public int getOrder_status() {
        return order_status;
    }

    public int getPayment_status() {
        return payment_status;
    }

    public int getTracking_status() {
        return tracking_status;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getRating() {
        return rating;
    }

    public String getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "OrderData{" +
                "id=" + id +
                ", desert_id=" + desert_id +
                ", uid='" + uid + '\'' +
                ", image='" + image + '\'' +
                ", desert_name='" + desert_name + '\'' +
                ", description='" + description + '\'' +
                ", customization='" + customization + '\'' +
                ", price='" + price + '\'' +
                ", quantity='" + quantity + '\'' +
                ", on_date='" + on_date + '\'' +
                ", instruction='" + instruction + '\'' +
                ", design='" + design + '\'' +
                ", add_on_price='" + add_on_price + '\'' +
                ", order_status=" + order_status +
                ", payment_status=" + payment_status +
                ", tracking_status=" + tracking_status +
                ", created_on='" + created_on + '\'' +
                ", rating='" + rating + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}

