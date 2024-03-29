package com.aston.dz1;


import java.util.Arrays;

@SuppressWarnings("unchecked")


public class ArrayList<T> implements List<T>{
    private T[] elements;
    private int capacity;
    private int size;
    private final static int DEFAULT_SIZE = 10;


    public ArrayList(){
        elements =  (T[]) new Object[DEFAULT_SIZE];
        capacity = DEFAULT_SIZE;
        size = 0;
    }

    public ArrayList(int capacity){
        elements =  (T[]) new Object[capacity];
        this.capacity = capacity;
        size = 0;
    }
    public ArrayList(T[] array){
        elements = Arrays.copyOf(array, array.length * 2);
        capacity = array.length * 2;
        size = array.length;
    }

    public ArrayList(List<T> list){
        T[] array = list.toArray();
        elements = Arrays.copyOf(array, array.length * 2);
        capacity = array.length * 2;
        size = array.length;
    }

    @Override
    public void add(T element) {
        if (isFull()) expandArray();
        elements[size++] = element;
    }

    @Override
    public void add(int index, T element) {
        if (isFull()) expandArray();
        if (elements[index] != null) {
            pushBack(index);
        }
        elements[index] = element;
    }

    @Override
    public void addAll(List<T> list) {
        while(size + list.size() > capacity){
            expandArray();
        }
        T[] array = list.toArray();
        for (T element : array){
            add(element);
        }
    }

    @Override
    public void addAll(T[] array) {
        while(size + array.length > capacity){
            expandArray();
        }
        for (T element : array){
            add(element);
        }
    }

    @Override
    public T[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for array list with size = " + size );
        return elements[index];
    }

    @Override
    public void deleteFirst(T element) {
        for (int i = 0; i < size; i ++){
            if(elements[i].equals(element)){
               elements[i] = null;
               pullUp(i);
               return;
            }
        }
        throw new ElementNotFoundException("Element " + element + " is not present in this list");
    }

    @Override
    public List<T> subList(int startIndex, int endIndex) {
        if (endIndex < startIndex) throw new IllegalArgumentException("End index must be greater than start index");
        ArrayList<T> subList = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++){
            subList.add(get(i));
        }
        return subList;
    }




    @Override
    public void clear() {
        elements = (T[]) new Object[DEFAULT_SIZE];
        capacity = DEFAULT_SIZE;
        size = 0;
    }


    @Override
    public int size() {
        return size;
    }

    private boolean isFull(){
        return elements.length >= 0.75 * capacity;
    }

    private void expandArray() {
        T[] newArray = (T[]) new Object[capacity * 2];
        System.arraycopy(elements, 0, newArray, 0, elements.length);
        elements = newArray;
        capacity *= 2;
    }

    private void pushBack(int index){
        for (int i = size-1; i >= index; i--){
            elements[i + 1] = elements[i];
        }
        elements[index] = null;
        size++;
    }

    private void pullUp(int index) {
        for (int i = index; i < size; i++){
            elements[i] = elements[i+1];
        }
        elements[size] = null;
        size--;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        if (size > 0){
            for (int i = 0; i < size-1; i++)
            {
                stringBuilder.append(elements[i].toString()).append(", ");
            }
            stringBuilder.append(elements[size-1]);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
