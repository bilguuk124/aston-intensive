package com.aston.dz1;

/**
 * com.aston.dz1.List interface
 * @param <T>
 */

public interface List<T> {

    /**
     * Add an element at the end of the list
     * @param element to add
     */
    void add(T element);

    /**
     * Add an element by index
     * @param index where to add
     * @param element to add
     */
    void add(int index, T element);

    /**
     * Adds all elements in the list
     * @param list to copy the elements from
     */
    void addAll(List<T> list);

    /**
     * Adds all elements in an array
     * @param array to copy the elements from
     */
    void addAll(T[] array);

    /**
     * Get an element by index
     * @param index of an element
     */
    T get(int index);

    /**
     * Remove first element that is equal to the element
     * @param element to delete
     */
    void deleteFirst(T element);


    /**
     * Get a part of the list from start to end
     * @param startIndex of the sublist
     * @param endIndex of the sublist
     * @return a part of the list from start (inclusive) to end indices (exclusive)
     */
    List<T> subList(int startIndex, int endIndex);

    /**
     * Get elements as an array
     * @return array of elements
     */
    T[] toArray();

    /**
     * Remove every element in the list
     */
    void clear();

    /**
     * Get size of the list
     * @return size of the list
     */
    int size();
}
