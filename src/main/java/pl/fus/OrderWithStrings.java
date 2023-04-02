package pl.fus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderWithStrings {
    private String orderId;
    private String orderValue;
    private String pickingTime;
    private String completeBy;

}
