package pl.fus;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
//        String test_data_file = "complete-by";
        String test_data_file = "logic-bomb";
        String orderPath = String.format("src/main/resources/self-test-data/%s/orders.json", test_data_file);
        String storePath = String.format("src/main/resources/self-test-data/%s/store.json", test_data_file);

        try {
            //Tworzenie obiektow na podstawie Json
            String orders_string = new String(Files.readAllBytes(Paths.get(orderPath)));
            OrderWithStrings[] ordersArray = new Gson().fromJson(orders_string, OrderWithStrings[].class);
            String store_string = new String(Files.readAllBytes(Paths.get(storePath)));
            StoreWithStrings storeWithString = new Gson().fromJson(store_string, StoreWithStrings.class);

            //Mapowanie obiektow
            List<Order> allOrdersList = new ArrayList<>();
            for (OrderWithStrings o : ordersArray) {
                allOrdersList.add(new Order(o));
            }
            Store store = new Store(storeWithString);


//                  ###################################################
            // wybieranie orderów w godzinach pracy sklepu LocalTime compareTo
//            List<Order> ordersToCompleteList = new ArrayList<>();
//
//            Predicate<Order> byStoreStartTime = order -> order.getCompleteBy().isAfter(store.getPickingStartTime()) ;
//            Predicate<Person> byStoreEndTime = person -> person.getAge() > 30;

//            List<Order> orderList = allOrdersList.stream()
//                    .filter(order -> order.getCompleteBy().compareTo(store.getPickingStartTime()) <= 0)
//                    .filter(order -> order.getCompleteBy().isAfter(store.getPickingStartTime()) && order.getCompleteBy().compareTo(store.getPickingStartTime()) == 0)
//                    .filter(byStoreStartTime)
//                    .filter(order -> order.getCompleteBy().compareTo(store.getPickingEndTime()) >= 0)
//                    .collect(Collectors.toList());
//                  ###################################################

            // Mapowanie
            Map<LocalTime, ArrayList<Order>> orderMap = new TreeMap<>();
            for (Order o : allOrdersList) {
                if (!orderMap.containsKey(o.getCompleteBy()))
                    orderMap.put(o.getCompleteBy(), new ArrayList<>(List.of(o)));
                else
                    orderMap.get(o.getCompleteBy()).add(o);
            }

            //Filtrowanie mapy w godzinach kompletowania zamowień


            //Wybieranie kolejnego rekordu z treemap
            Set<Map.Entry<LocalTime, ArrayList<Order>>> collect = orderMap.entrySet()
                    .stream()
                    .filter(record -> record.getKey().compareTo(store.getPickingStartTime()) >= 0)
                    .filter(record -> record.getKey().compareTo(store.getPickingEndTime()) <= 0)
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (oldValue, newValue) -> newValue,
                                    TreeMap::new)
                    ).entrySet();
            //collect wypluwa uporządkowaną liste poprawnych wynikow

            List<Order> test = new ArrayList<>();
            LocalTime nextOrderAt;
            if(!collect.isEmpty() &&  collect.iterator().hasNext()){
                nextOrderAt = collect.iterator().next().getKey();
                List<Order> numOfOrdersAtThatTime = collect.iterator().next().getValue();
                for (int i = 0; i < numOfOrdersAtThatTime.size(); i++) {
                    if(numOfOrdersAtThatTime.get(i).getPickingTime().compareTo(LocalTime.of(0,0)) == 0)
                        test.add(numOfOrdersAtThatTime.get(i));
                    else {
                        test.add(numOfOrdersAtThatTime.get(i));
//                        nextOrderAt = LocalTime.
                    }
                }
//                collect.iterator().next().getValue().;
            }
//            Set<Map.Entry<LocalTime, ArrayList<Order>>> entries = collect.entrySet();

//            Map<LocalTime, ArrayList<Order>> treeMap = collect.entrySet()
//
//                    .stream()
//                    .collect(
//                            Collectors.toMap(
//                                    Map.Entry::getKey,
//                                    Map.Entry::getValue,
//                                    (oldValue, newValue) -> newValue,
//                                    TreeMap::new)
//                    );

            //Lista w danych godzinach
//            Map<LocalTime, ArrayList<OrderFixed>> collect2 = orderMap.entrySet()
//                    .stream()
//                    .filter(record -> record.getKey().compareTo(LocalTime.of(9, 10)) >= 0)
//
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

//            Comparator<Order> orderFixedComparator = Comparator
//                    .comparing((Order order) -> order.getPickingTime());
////                    .comparing((OrderFixed orderFixed) -> order.getCompleteBy())
//            List<Order> employeeOrderList = new ArrayList<>();
//            LocalTime temp;
//            for (LocalTime t : treeMap.keySet()) {
//                List<Order> tempOrderFixed = treeMap.get(t);
////                tempOrderFixed.stream().min(Comparator.comparingInt(value -> value.getPickingTime()));
//            }

//            pickOrders(treeMap);


//            employeeOrderList.a

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            System.out.println(timeElapsed);

//            Comparator<OrderFixed> orderFixedComparator = Comparator
//                    .comparing((OrderFixed orderFixed) -> orderFixed.getCompleteBy())
//                    .thenComparing((OrderFixed orderFixed) -> orderFixed.getPickingTime())
//                    .thenComparing((OrderFixed orderFixed) -> orderFixed.getOrderValue());
//
//            Collections.sort(orderFixedList, orderFixedComparator);

            System.out.println();

//            //
//            String store_string = new String(Files.readAllBytes(Paths.get(storePath)));
////            Store store = new Gson().fromJson(store_string, Store.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static List<Order> pickOrders(TreeMap<LocalTime, ArrayList<Order>> map) {

        return null;
    }

}