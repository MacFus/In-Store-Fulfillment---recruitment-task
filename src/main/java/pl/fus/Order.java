package pl.fus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Order {
    private String orderId;
    private String orderValue;
    private String pickingTime;
    private String completeBy;

}
