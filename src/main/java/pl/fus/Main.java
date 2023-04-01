package pl.fus;

import com.google.gson.Gson;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Time;

public class Main {
    public static void main(String[] args) {
        String test_data_file = "complete-by";
        String orderPath = String.format("src/main/resources/self-test-data/%s/orders.json", test_data_file);
        String storePath = String.format("src/main/resources/self-test-data/%s/store.json", test_data_file);

        try {
            //
            String orders_string = new String(Files.readAllBytes(Paths.get(orderPath)));
            Order[] orders = new Gson().fromJson(orders_string, Order[].class);


            //
            String store_string = new String(Files.readAllBytes(Paths.get(storePath)));
//            Store store = new Gson().fromJson(store_string, Store.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}