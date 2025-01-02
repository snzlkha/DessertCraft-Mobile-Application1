package com.yourapp.desertcraftadmin.model;

public class OrderData {
    String id;
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
    String receipt;
    String created_on;

    public OrderData(String id, String uid, String image, String desert_name, String description, String customization, String price, String quantity, String on_date, String instruction, String design, String add_on_price, int order_status, int payment_status, int tracking_status, String receipt, String created_on) {
        this.id = id;
        this.uid = uid;
        this.image = image;
        this.desert_name = desert_name;
        this.description = description;
        this.customization = customization;
        this.price = price;
        this.quantity = quantity;
        this.on_date = on_date;
        this.instruction = instruction;
        this.design = design;
        this.add_on_price=add_on_price;
        this.order_status = order_status;
        this.payment_status = payment_status;
        this.tracking_status = tracking_status;
        this.receipt=receipt;
        this.created_on = created_on;
    }

    public String getId() {
        return id;
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

    public String getReceipt() {
        return receipt;
    }

    public String getCreated_on() {
        return created_on;
    }

    @Override
    public String toString() {
        return "OrderData{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", image='" + image + '\'' +
                ", desert_name='" + desert_name + '\'' +
                ", description='" + description + '\'' +
                ", customization='" + customization + '\'' +
                ", price=" + price +
                ", quantity='" + quantity + '\'' +
                ", on_date='" + on_date + '\'' +
                ", instruction='" + instruction + '\'' +
                ", design='" + design + '\'' +
                ", add_on_price=" + add_on_price +
                ", order_status=" + order_status +
                ", payment_status=" + payment_status +
                ", tracking_status=" + tracking_status +
                ", receipt=" + receipt +
                ", created_on='" + created_on + '\'' +
                '}';
    }
}

