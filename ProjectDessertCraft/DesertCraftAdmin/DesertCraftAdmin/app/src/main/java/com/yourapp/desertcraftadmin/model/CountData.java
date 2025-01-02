package com.yourapp.desertcraftadmin.model;

public class CountData {
    String total_pending_approval;
    String total_accepted;
    String total_need_pay;
    String total_in_kitchen;
    String total_ready_pickup;
    String total_completed;
    String total_cancelled;
    String total_review;

    public CountData(String total_pending_approval, String total_accepted, String total_need_pay, String total_in_kitchen, String total_ready_pickup, String total_completed, String total_cancelled, String total_review) {
        this.total_pending_approval = total_pending_approval;
        this.total_accepted = total_accepted;
        this.total_need_pay = total_need_pay;
        this.total_in_kitchen = total_in_kitchen;
        this.total_ready_pickup = total_ready_pickup;
        this.total_completed = total_completed;
        this.total_cancelled = total_cancelled;
        this.total_review = total_review;
    }

    public String getTotal_pending_approval() {
        return total_pending_approval;
    }

    public String getTotal_accepted() {
        return total_accepted;
    }

    public String getTotal_need_pay() {
        return total_need_pay;
    }

    public String getTotal_in_kitchen() {
        return total_in_kitchen;
    }

    public String getTotal_ready_pickup() {
        return total_ready_pickup;
    }

    public String getTotal_completed() {
        return total_completed;
    }

    public String getTotal_cancelled() {
        return total_cancelled;
    }

    public String getTotal_review() {
        return total_review;
    }

    @Override
    public String toString() {
        return "CountData{" +
                "total_pending_approval='" + total_pending_approval + '\'' +
                ", total_accepted='" + total_accepted + '\'' +
                ", total_need_pay='" + total_need_pay + '\'' +
                ", total_in_kitchen='" + total_in_kitchen + '\'' +
                ", total_ready_pickup='" + total_ready_pickup + '\'' +
                ", total_completed='" + total_completed + '\'' +
                ", total_cancelled='" + total_cancelled + '\'' +
                ", total_review='" + total_review + '\'' +
                '}';
    }
}
