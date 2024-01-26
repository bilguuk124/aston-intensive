package com.aston.dz1;

public class QuickSort<T extends Comparable<T>> implements Sort<T>{


    @Override
    public List<T> sort(List<T> list) {
        if (list == null || list.size() <= 1){
            return list;
        }

        List<T> less = new ArrayList<>();
        List<T> equal = new ArrayList<>();
        List<T> greater = new ArrayList<>();

        T pivot = list.get(list.size() / 2);
        for (int i = 0; i < list.size(); i ++){
            T element = list.get(i);
            int cmp = element.compareTo(pivot);
            if (cmp < 0) {
                less.add(element);
            } else if (cmp > 0){
                greater.add(element);
            } else {
                equal.add(element);
            }
        }

        List<T> sortedList = new ArrayList<>();
        sortedList.addAll(sort(less));
        sortedList.addAll(equal);
        sortedList.addAll(sort(greater));
        return sortedList;
    }
}
