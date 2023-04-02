package pl.fus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
public class Store {
    private List<String> pickers;
    private LocalTime pickingStartTime;
    private LocalTime pickingEndTime;

    public Store(StoreWithStrings store) {
        this.pickers = store.getPickers();
        this.pickingStartTime = LocalTime.parse(store.getPickingStartTime());
        this.pickingEndTime = LocalTime.parse(store.getPickingEndTime());
    }


}
