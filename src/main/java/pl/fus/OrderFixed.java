package pl.fus;

import java.time.LocalTime;

public class OrderFixed {
    private String orderId;
    private double orderValue;
    private int pickingTime;
    private LocalTime completeBy;

    public OrderFixed(Order order) {
        this.orderId = order.getOrderId();
        this.orderValue = Double.valueOf(order.getOrderValue());
        this.pickingTime = transformToMinFormat(order.getPickingTime());
        this.completeBy = LocalTime.parse(order.getCompleteBy());
    }

    private int transformToMinFormat(String time) {
        return time.equals("PT0S") ? 0 : Integer.valueOf(time.replace("PT", "").replace("M", ""));
    }
}
