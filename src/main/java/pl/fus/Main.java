package pl.fus;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
//        String test_data_file = "complete-by";
//        String test_data_file = "optimize-order-count";
        String test_data_file = "logic-bomb";
//        String test_data_file = "advanced-allocation";
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
            Map<LocalTime, ArrayList<Order>> orderHashMap = orderMap.entrySet()
                    .stream()
                    .filter(record -> record.getKey().compareTo(store.getPickingStartTime()) >= 0)
                    .filter(record -> record.getKey().compareTo(store.getPickingEndTime()) <= 0)
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    .collect(
                            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                    );
            TreeMap<LocalTime, ArrayList<Order>> treeMap = new TreeMap<LocalTime, ArrayList<Order>>();
            treeMap.putAll(orderHashMap);

            //collect wypluwa uporządkowaną liste poprawnych wynikow



            /*
            List<Student> sorted = students.stream()
                    .map(f -> new Student(f.getId(), f.getSubjects().stream().sorted(Comparator.comparing(Subject::getSubjectCode)).collect(Collectors.toList())))
                    .sorted(Comparator.comparing(Student::getRollNo))
                    .collect(Collectors.toList())
                    */

            ArrayList<Order> officialList = new ArrayList<>();
            LocalTime nextOrderAt = treeMap.firstKey();
            ArrayList<Order> orders = prepareListOfOrders(officialList, treeMap, nextOrderAt, store.getPickingEndTime());
            System.out.println(orders);
//            if (!orderSet.isEmpty() && orderSet.iterator().hasNext()) {
//                //sortowanie orderów na daną godzine po pickingTime
//                List<Order> sortedOrders = orderSet.iterator().next().getValue()
//                        .stream()
//                        .sorted(Comparator.comparing(Order::getPickingTime))
//                        .collect(Collectors.toList());
////                nextOrderAt
//                for (int i = 0; i < sortedOrders.size(); i++) {
//                    if (sortedOrders.get(i).getPickingTime().isZero()) {
//                        test.add(sortedOrders.get(i));
//                    } else {
//                        test.add(sortedOrders.get(i));
////                         nextOrderAt = sortedOrders.get(i).getPickingTime().plus(sortedOrders.get(i).getPickingTime());
//                        break;
//                    }
//                }
//            }
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
//            Map<LocalTime, ArrayList<Order>> collect2 = orderMap.entrySet()
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

//            //
//            String store_string = new String(Files.readAllBytes(Paths.get(storePath)));
////            Store store = new Gson().fromJson(store_string, Store.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static ArrayList<Order> prepareListOfOrders(ArrayList<Order> list, TreeMap<LocalTime, ArrayList<Order>> orderMap, LocalTime nextOrderAt, LocalTime endOfPacking) {
        if (nextOrderAt.isBefore(endOfPacking)) {
            nextOrderAt = orderMap.ceilingKey(nextOrderAt);
            ArrayList<Order> orderArrayList = orderMap.get(nextOrderAt);
//        while (!orderMap.isEmpty() && orderMap.iterator().hasNext()) {
//            //sortowanie orderów na daną godzine po pickingTime
////            if(orderSet.contains())
//            Map.Entry<LocalTime, ArrayList<Order>> order = orderMap.iterator().next();
            List<Order> sortedOrders = orderArrayList
                    .stream()
                    .sorted(Comparator.comparing(Order::getPickingTime))
                    .collect(Collectors.toList());
            for (int i = 0; i < sortedOrders.size(); i++) {
                if (sortedOrders.get(i).getPickingTime().isZero()) {
                    list.add(sortedOrders.get(i));
                    orderMap.get(nextOrderAt).remove(sortedOrders.get(i));
                } else {
                    if (nextOrderAt.compareTo(endOfPacking) >= 0)
                        return list;
                    list.add(sortedOrders.get(i));
                    orderMap.get(nextOrderAt).remove(sortedOrders.get(i));
                    nextOrderAt = nextOrderAt.plus(sortedOrders.get(i).getPickingTime());

                    break;
                }
            }
            prepareListOfOrders(list, orderMap, nextOrderAt, endOfPacking);
        }
        return list;
    }
}

