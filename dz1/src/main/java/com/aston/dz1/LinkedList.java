package com.aston.dz1;

@SuppressWarnings("unchecked")
// тут реализация хорошая, но ошибки по стилю такие же как в ArrayList, поэтому смотри туда)
public class LinkedList<T> implements List<T>{
    private Node<T> head;
    private int size;

    public LinkedList(){
        head = null;
        size = 0;
    }

    public LinkedList(T element){
        head = new Node<>(element, null);
        size = 0;
    }

    public LinkedList(T[] array){
        for (T element : array){
            add(element);
        }
    }

    public LinkedList(List<T> list){
        T[] array = list.toArray();
        for (T element : array){
            add(element);
        }
    }

    @Override
    public void add(T element) {
        if (head == null){
            head = new Node<>(element, null);
        } else{
            Node<T> current = head;
            while(current.next != null){
                current = current.next;
            }
            current.next = new Node<>(element, current);
        }
        size++;
    }

    @Override
    public void add(int index, T element) {
        if (index >= size) throw new IndexOutOfBoundsException("Index " + index + " is out of bound for size " + size);
        Node<T> current = head;
        for(int i = 0; i < index; i++){
            current = current.next;
        }
        current.previous.next = new Node<>(element, current.previous, current);
        current.previous = current.previous.next;
        size++;
    }

    @Override
    public void addAll(List<T> list) {
        T[] array = list.toArray();
        for (T element : array){
            add(element);
        }
    }

    @Override
    public void addAll(T[] array) {
        for (T element : array){
            add(element);
        }
    }

    @Override
    public T get(int index) {
        if (index >= size) throw new IndexOutOfBoundsException("Index " + index + " is out of bound for size " + size);
        Node<T> current = head;
        for(int i = 0; i < index; i++){
            current = current.next;
        }
        return current.data;
    }

    @Override
    public void deleteFirst(T element) {
        if (head.data.equals(element)){
            if (head.next == null){
                head = null;
                size = 0;
            }
            else{
                head.next.previous = null;
                head = head.next;
                size--;
            }
            return;
        }
        Node<T> current = head;
        while(current != null){
            if (current.data.equals(element)){
                current.previous.next = current.next;
                current.next.previous = current.previous;
                size--;
                return;
            }
            current = current.next;
        }
        throw new ElementNotFoundException("Element " + element + " does not exist");
    }

    @Override
    public List<T> subList(int startIndex, int endIndex) {
        if (endIndex < startIndex) throw new IllegalArgumentException("End index must be greater than start index");
        if (startIndex > size || endIndex > size) throw new IndexOutOfBoundsException("Indices must be less than size of the list");
        LinkedList<T> subList = new LinkedList<>();
        Node<T> current = head;
        int index = 0;
        while(index < startIndex) {
            index++;
            current = current.next;
        }
        while(current != null && index < endIndex){
            subList.add(current.data);
            current = current.next;
            index++;
        }
        return subList;
    }

    @Override
    public T[] toArray() {
        T[] array = (T[]) new Object[size];
        if (head == null) return array;
        Node<T> current = head;
        int index = 0;
        while(current != null){
            array[index] = current.data;
            index++;
            current = current.next;
        }
        return array;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        if (head != null){
            Node<T> current = head;
            while(current.next != null){
                stringBuilder.append(current.data.toString()).append(", ");
                current = current.next;
            }
            stringBuilder.append(current.data);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> previous;

        protected Node(T data, Node<T> previous){
            this.data = data;
            this.previous = previous;
            this.next = null;
        }

        protected Node(T data, Node<T> previous, Node<T> next){
            this.data = data;
            this.previous = previous;
            this.next = next;
        }
    }


}
