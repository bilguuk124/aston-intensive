package com.aston.dz1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SortTest {

    List<Integer> linkedList = new LinkedList<>();
    List<Integer> arrayList = new LinkedList<>();
    Integer[] array = new Integer[]{9, 3, 2, 8, 5, 1, 6, 0, 7, 4, 1000};
    Integer[] sortedArray = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1000};

    @BeforeEach
    void init(){
        linkedList.clear();
        linkedList.addAll(array);
        arrayList.clear();
        arrayList.addAll(array);
    }

    @Test
    void quickSortLinkedList(){
        Sort<Integer> sorter = new QuickSort<>();
        List<Integer> sortedList = sorter.sort(linkedList);
        assertArrayEquals(sortedArray, sortedList.toArray());
    }

    @Test
    void mergeSortLinkedList(){
        Sort<Integer> sorter = new MergeSort<>();
        List<Integer> sortedList = sorter.sort(linkedList);
        System.out.println(Arrays.toString(sortedList.toArray()));
        assertArrayEquals(sortedArray, sortedList.toArray());
    }

    @Test
    void quickSortArrayList(){
        Sort<Integer> sorter = new QuickSort<>();
        List<Integer> sortedList = sorter.sort(arrayList);
        assertArrayEquals(sortedArray, sortedList.toArray());
    }

    @Test
    void mergeSortArrayList(){
        Sort<Integer> sorter = new MergeSort<>();
        List<Integer> sortedList = sorter.sort(arrayList);
        System.out.println(Arrays.toString(sortedList.toArray()));
        assertArrayEquals(sortedArray, sortedList.toArray());
    }
}
