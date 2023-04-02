package pl.fus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
@ToString
public class Order {
    private String orderId;
    private double orderValue;
    private Duration pickingTime;
    private LocalTime completeBy;

    public Order(OrderWithStrings order) {
        this.orderId = order.getOrderId();
        this.orderValue = Double.parseDouble(order.getOrderValue());
        this.pickingTime = Duration.parse(order.getPickingTime());
//        this.pickingTime = LocalTime.of(0,transformToMinFormat(order.getPickingTime()));
        this.completeBy = LocalTime.parse(order.getCompleteBy());
    }

    private int transformToMinFormat(String time) {
        return time.equals("PT0S") ? 0 : Integer.parseInt(time.replace("PT", "").replace("M", ""));
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", orderValue=" + orderValue +
                ", pickingTime=" + pickingTime +
                ", completeBy=" + completeBy +
                "}\n";
    }
}
