package pl.fus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@Getter
public class Store {
    private List<Picker> pickers;
    private String pickingStartSTime;
    private String pickingEndTime;
}
