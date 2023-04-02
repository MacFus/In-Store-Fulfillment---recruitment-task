package pl.fus;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        // OK: complete-by, isf-end-time, any-order-length-is-ok, optimize-order-count
//        String test_data_file = "advanced-allocation";
        String test_data_file = "advanced-optimize-order-count";
//        String test_data_file = "advanced-optimize-order-value";
//        String test_data_file = "logic-bomb";
//        String test_data_file = "optimize-order-value";

//        String test_data_file = "optimize-order-count";
//        String test_data_file = "any-order-length-is-ok";
//        String test_data_file = "complete-by";
//        String test_data_file = "isf-end-time";
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
            TreeMap<LocalTime, ArrayList<Order>> treeMap = new TreeMap<>(orderHashMap);

            /*
            List<Student> sorted = students.stream()
                    .map(f -> new Student(f.getId(), f.getSubjects().stream().sorted(Comparator.comparing(Subject::getSubjectCode)).collect(Collectors.toList())))
                    .sorted(Comparator.comparing(Student::getRollNo))
                    .collect(Collectors.toList())
                    */

//            ArrayList<Order> officialList = new ArrayList<>();
//            LocalTime nextOrderAt = treeMap.firstKey();
//            ArrayList<Order> orders = prepareListOfOrders(officialList, treeMap, nextOrderAt, store);
//            System.out.println(orders);

            ordersForPickers(store, treeMap);
//                findCombination(treeMap, store);

//            Comparator<Order> orderFixedComparator = Comparator
//                    .comparing((Order order) -> order.getPickingTime());
////                    .comparing((OrderFixed orderFixed) -> order.getCompleteBy())
//            List<Order> employeeOrderList = new ArrayList<>();
//            LocalTime temp;
//            for (LocalTime t : treeMap.keySet()) {
//                List<Order> tempOrderFixed = treeMap.get(t);
////                tempOrderFixed.stream().min(Comparator.comparingInt(value -> value.getPickingTime()));
//            }

            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            System.out.println(timeElapsed);

//            Comparator<OrderFixed> orderFixedComparator = Comparator
//                    .comparing((OrderFixed orderFixed) -> orderFixed.getCompleteBy())
//                    .thenComparing((OrderFixed orderFixed) -> orderFixed.getPickingTime())
//                    .thenComparing((OrderFixed orderFixed) -> orderFixed.getOrderValue());
//
//            Collections.sort(orderFixedList, orderFixedComparator);

        } catch (IOException | StackOverflowError e) {
            e.printStackTrace();
        }


    }

    public static ArrayList<Order> prepareListOfOrders(ArrayList<Order> list, TreeMap<LocalTime, ArrayList<Order>> orderMap, LocalTime nextOrderAt, Store store) {
        LocalTime start;
        if (list.isEmpty()) {
            start = store.getPickingStartTime();
        } else {
            start = list.get(list.size() - 1).getCompleteBy();
        }
//
//        if (nextOrderAt.compareTo(store.getPickingEndTime()) > 0) {
//            System.out.println("Next order after pickingEndTime");
//            return list;
//        }

//        else if (nextOrderAt.compareTo(store.getPickingEndTime()) == 0) {
//        }


        // map contains key
        if (orderMap.containsKey(nextOrderAt)) {
            List<Order> sortedOrders = sortOrders(orderMap, nextOrderAt);

            // no more orders at that time
            if (sortedOrders.isEmpty()) {
                orderMap.remove(nextOrderAt);
                prepareListOfOrders(list, orderMap, nextOrderAt, store);
            } else {
                for (Order o : sortedOrders) {
//                    if (!list.isEmpty()) {
//                        for (Order orderToFill : sortedOrders){
//                            orderToFill.
//                        }
//                    }

                    // if picking time == 0
                    if (o.getPickingTime().isZero()) {
                        o.setPickingStartTime(start);
                        list.add(o);
                        orderMap.get(nextOrderAt).remove(o);
                        // if picking time != 0
                    } else {
                        //check if it's possible to complete order
                        if (nextOrderAt.compareTo(store.getPickingEndTime()) > 0)
                            return list;
                        // if contains but cant complete order
                        if (start.plus(sortedOrders.get(0).getPickingTime()).isBefore(store.getPickingEndTime())) {
                            o.setPickingStartTime(start);
                            list.add(o);
                            orderMap.get(nextOrderAt).remove(o);
                            nextOrderAt = nextOrderAt.plus(o.getPickingTime());
                            break;

                        }
                        return list;

                    }
                }
                prepareListOfOrders(list, orderMap, nextOrderAt, store);
            }
        }

        // map doesnt contain key
        else {
            // if last key from the map is lower than nextOrderAt return list
            if (orderMap.lastEntry().getKey().isBefore(nextOrderAt))
                return list;
//            if(orderMap.lastEntry().getKey().equals(nextOrderAt) && orderMap.get(nextOrderAt).get(0).)
//            if (!orderMap.containsKey(nextOrderAt) && nextOrderAt.isAfter(store.getPickingEndTime()))
//
            // if order key does not exists, try higher value
            nextOrderAt = orderMap.ceilingKey(nextOrderAt);
            prepareListOfOrders(list, orderMap, nextOrderAt, store);
        }
        return list;
    }


//        LocalTime startPicking = nextOrderAt;
//        if (orderMap.containsKey(nextOrderAt)) {
//
//            nextOrderAt = orderMap.ceilingKey(nextOrderAt);
//
//            List<Order> sortedOrders = sortOrders(orderMap, nextOrderAt);
//
//            if (sortedOrders.size() == 0) {
//                nextOrderAt = orderMap.ceilingKey(nextOrderAt);
//                orderMap.remove(nextOrderAt);
//                prepareListOfOrders(list, orderMap, nextOrderAt, store);
//            } else {
//                for (int i = 0; i < sortedOrders.size(); i++) {
//                    if (sortedOrders.get(i).getPickingTime().isZero()) {
//                        sortedOrders.get(i).setPickingStartTime(startPicking);
//                        list.add(sortedOrders.get(i));
//                        orderMap.get(nextOrderAt).remove(sortedOrders.get(i));
//                    } else {
//                        if (nextOrderAt.compareTo(store.getPickingEndTime()) >= 0)
//                            return list;
//                        sortedOrders.get(i).setPickingStartTime(startPicking);
//                        list.add(sortedOrders.get(i));
//                        orderMap.get(nextOrderAt).remove(sortedOrders.get(i));
//                        nextOrderAt = nextOrderAt.plus(sortedOrders.get(i).getPickingTime());
//
//                        break;
//                    }
//                    return list;
//                }
//            }
//
//            prepareListOfOrders(list, orderMap, nextOrderAt, store);
//        } else {
//            if (orderMap.ceilingKey(nextOrderAt).equals(null))
////            nextOrderAt = orderMap.ceilingKey(nextOrderAt);
////            if (!orderMap.containsKey(nextOrderAt))
//                return list;
//            prepareListOfOrders(list, orderMap, nextOrderAt, store);
//        }
//
//        return list;
//    }

    public static void ordersForPickers(Store store, TreeMap<LocalTime, ArrayList<Order>> orderMap) {
        Map<String, ArrayList<Order>> map = new HashMap<>();
        for (String picker : store.getPickers()) {
            ArrayList<Order> orders1 = new ArrayList<>();
            ArrayList<Order> orders = prepareListOfOrders(orders1, orderMap, store.getPickingStartTime(), store);
            map.put(picker, orders);
        }
        for (String key : map.keySet()) {
            for (int i = 0; i < map.get(key).size(); i++) {
                System.out.println(key + " " + map.get(key).get(i).getOrderId() + " " + map.get(key).get(i).getPickingStartTime());
            }
        }


    }

    private static ArrayList<Order> sortOrders(TreeMap<LocalTime, ArrayList<Order>> orderMap, LocalTime time) {
        ArrayList<Order> orderArrayList = orderMap.get(time);
        List<Order> sortedOrders = orderArrayList
                .stream()
                .sorted(Comparator.comparing(Order::getPickingTime))
                .collect(Collectors.toList());
        return (ArrayList<Order>) sortedOrders;
    }

    private static ArrayList<Order> findCombination(TreeMap<LocalTime, ArrayList<Order>> orderMap, Store store) {
        long until = store.getPickingStartTime().until(store.getPickingEndTime(), ChronoUnit.MINUTES);
        orderMap.keySet();
        for (LocalTime lt : orderMap.keySet()){

        }
        return null;
    }
}

