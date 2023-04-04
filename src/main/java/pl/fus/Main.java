package pl.fus;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        // OK: complete-by, isf-end-time, any-order-length-is-ok, optimize-order-count
        String test_data_file = "advanced-allocation";
//        String test_data_file = "advanced-optimize-order-count";
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

//            ordersForPickers(store, treeMap);
            advancedAllocation(treeMap, store);
//            findCombination(treeMap, store);

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

    public static void advancedAllocation(TreeMap<LocalTime, ArrayList<Order>> orderMap, Store store) {
        Collection<ArrayList<Order>> orders = orderMap.values();
        ArrayList<Order> orderList = new ArrayList<>();
        List<Order> tempList = new ArrayList<>();
        for (ArrayList<Order> order : orders)
            for (Order o : order)
                orderList.add(o);
        Order[] orderArray = new Order[orderList.size()];
        for (int i = 0; i < orderList.size(); i++) {
            orderArray[i] = orderList.get(i);
        }
        HashMap<String, List<Order>> hashMap = new HashMap<>();
        HashMap<String, List<Order>> hashMap2 = sortArray(hashMap, store, orderList);
//        Order[] objects = (Order[]) orderList.toArray();
        System.out.println();
    }

    public static HashMap<String, List<Order>> sortArray(HashMap<String, List<Order>> hashMap, Store store, ArrayList<Order> orders) {
        LocalTime start = store.getPickingStartTime();
        LocalTime end = store.getPickingEndTime();
        String[] pickers = new String[store.getPickers().size()];
        // initialize 'pickers' array with picker names
        for (int i = 0; i < store.getPickers().size(); i++) {
            pickers[i] = store.getPickers().get(i);
        }
        // start time for each picker
        // initialize with store.pickingStartTime
        List<LocalTime> pickersTime = new ArrayList<>(Collections.nCopies(pickers.length, start));

        // sort each time when removing order
        while (orders.size() != 0) {

            // order ; p1 ; p2
            //  0       0    0
            Long[][] pickersTimeAndCompleteByDiff = new Long[orders.size()][pickers.length];
            HashMap<String, ArrayList<Long>> map = new HashMap<>();
            for (int i = 0; i < pickers.length; i++) {
                for (int j = 0; j < orders.size(); j++) {

//                    if(j==0)
//                        pickersTimeAndCompleteByDiff = new Long[orders.size()][pickers.length];
                    // order.CompleteBy - (picker time + order.pickingTime)
                    long difference = orders.get(j).getCompleteBy().until(pickersTime.get(i).plus(orders.get(j).getPickingTime()), ChronoUnit.MINUTES);

                    // fill with difference for each order
                    pickersTimeAndCompleteByDiff[j][i] = difference;


                    // if difference between (picking time + pickersTime) - orderCompleteBy == 0
//                    if (difference == 0) {
//                        // set start picking time
//                        orders.get(j).setPickingStartTime(pickersTime.get(i));
//                        // update List of pickersTime
//                        pickersTime.set(i, pickersTime.get(i).plus(orders.get(j).getPickingTime()));
//                        // save record in hashmap
//                        if (!hashMap.containsKey(pickers[i])) {
//                            hashMap.put(pickers[i], new ArrayList<>());
//                        }
//                        hashMap.get(pickers[i]).add(orders.get(j));
//                        // remove order from orders <List>
//                        orders.remove(j);
////                        pickersTimeAndCompleteByDiff = null;
////                        break;
//
//                        // else find min diff value from all orders
//                    } else if (j == orders.size()) {
////                        ArrayList<Integer> arrayList = getMaxValue(pickersTimeAndCompleteByDiff);
//                        Long maxValue = pickersTimeAndCompleteByDiff[0][0];
//
//                            for (int l = 0; l < orders.size(); l++) {
//                                // find index of first max value
//                                if(pickersTimeAndCompleteByDiff[i][l ] > maxValue)
//                                    maxValue = pickersTimeAndCompleteByDiff[l][i];
//                            }
//
//
////                        Map<Integer, Integer> maxValue = getMaxValue(pickersTimeAndCompleteByDiff);
//                    }

//                    if (orderArray.size() != orders.size())
//                        orderArray.add(untils);
//                    else
//                        orderArray.set(j, untils);
//                    if (orderArray.size() == orders.size()) {
//                        for (int k = 0; k < orders.size(); k++) {
//                            for (Long l : orderArray.get(k))
//                                System.out.println(l);
//                        }
//
//                        Long min = Collections.min(untils);
//                        for (int k = 0; k < untils.size(); k++) {
//                            if (untils.get(k) == min) {
//                                // set start picking time
//                                orders.get(j).setPickingStartTime(pickersTime.get(k));
//                                // update List of pickersTime
//                                pickersTime.set(k, pickersTime.get(k).plus(orders.get(j).getPickingTime()));
//                                // save record in hashmap
//                                if (!hashMap.containsKey(pickers[k]))
//                                    hashMap.put(pickers[k], new ArrayList<>(List.of(orders.get(j))));
//                                else
//                                    hashMap.get(pickers[k]).add(orders.get(j));
//                                orders.remove(j);
//                            }
//
//
//                        }
//                    }
//                    break
                }
            }
            ArrayList<Long> max = new ArrayList<>(pickers.length);
            for (int i = 0; i < orders.size(); i++) {
                for (int j = 0; j < pickers.length; j++) {
                    if (max.size() != pickers.length)
                        max.add(pickersTimeAndCompleteByDiff[i][j]);
                    else if (max.size() == pickers.length) {
                        if (pickersTimeAndCompleteByDiff[i][j] > max.get(j))
                            max.set(i, pickersTimeAndCompleteByDiff[i][j]);
                    }

                }

            }
            pickersTimeAndCompleteByDiff = null;
        }

        return null;
    }

    private static Map<Integer, Integer> getMaxValue(Long[][] numbers) {
        Long maxValue = numbers[0][0];
        HashMap<Integer, Integer> map = new HashMap<>();

        //        int i, j;
        for (int j = 0; j < numbers.length; j++) {
            for (int i = 0; i < numbers[j].length; i++) {
                if (numbers[j][i] > maxValue) {
                    maxValue = numbers[j][i];
                    map.put(j, i);
                }
            }
        }
        if (map.size() == 0)
            map.put(0, 0);
        return map;
    }

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
        orderMap.ceilingKey(store.getPickingStartTime());
        Collection<ArrayList<Order>> orders = orderMap.values();
        List<Order> orderList = new ArrayList<>();
        List<Order> tempList = new ArrayList<>();
        for (ArrayList<Order> order : orders)
            for (Order o : order)
                orderList.add(o);

        for (int i = 0; i < orderList.size(); i++) {
            tempList.add(orderList.get(0));

        }
        return null;
    }
}

