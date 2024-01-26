package com.aston.dz1;

public class MergeSort<T extends Comparable<T>> implements Sort<T>{

    private List<T> merge(List<T> left, List<T> right){
        List<T> result = new ArrayList<>();
        int i = 0, j = 0;

        while(i < left.size() && j < right.size()){
            if (left.get(i).compareTo(right.get(j)) <= 0){
                result.add(left.get(i));
                i++;
            }
            else{
                result.add(right.get(j));
                j++;
            }
        }

        while (i < left.size()){
            result.add(left.get(i));
            i++;
        }

        while(j < right.size()){
            result.add(right.get(j));
            j++;
        }


        return result;
    }

    @Override
    public List<T> sort(List<T> list) {
        int size = list.size();

        if (size <= 1){
            return list;
        }

        int mid = size / 2;

        List<T> left = sort(list.subList(0, mid));
        List<T> right = sort(list.subList(mid, size));

        return merge(left, right);
    }
}
