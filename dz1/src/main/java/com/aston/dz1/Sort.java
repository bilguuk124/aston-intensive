package com.aston.dz1;

public interface Sort <T extends Comparable<T>> {

    /**
     * Sorts the given list
     * @param list to sort to
     * @return new sorted list
     */
    List<T> sort(List<T> list);
}
