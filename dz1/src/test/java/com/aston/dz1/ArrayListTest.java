package com.aston.dz1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayListTest {

    private List<Integer> list = new ArrayList<>();

    // не надо каждый раз чистить лист, лучше сделать его нестатическим. Дело в том,
    // что на каждый метод теста для изоляции будет создаваться отдельный объект этого класса, следовательно и лист будет каждый раз новый.

    @Test
    void addOneInt() {
        list.add(1);
        assertEquals(1, list.get(0));
        assertEquals(1, list.size());
    }

    @Test
    void addMultipleInt() {
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        assertAll(
                () -> assertEquals(1, list.get(0)), // когда много ассертов необходимо запихивать их в assertAll
                () -> assertEquals(2, list.get(1)),
                () -> assertEquals(3, list.get(2)),
                () -> assertEquals(4, list.get(3)),
                () -> assertEquals(5, list.get(4)),
                () -> assertEquals(5, list.size())
        );

    }

    @Test
    void addAllFromList(){
        //given
        List<Integer> listToAdd = new LinkedList<>();
        listToAdd.add(1);
        listToAdd.add(10);
        listToAdd.add(100);
        listToAdd.add(1000);

        //when
        list.addAll(listToAdd);

        //then
        assertEquals(1, list.get(0));
        assertEquals(10, list.get(1));
        assertEquals(100, list.get(2));
        assertEquals(1000, list.get(3));
        assertEquals(4, list.size());
    }

    @Test
    void addAllFromListMoreThanCapacity(){
        list = new ArrayList<>(2);
        List<Integer> listToAdd = new LinkedList<>();
        listToAdd.add(1);
        listToAdd.add(10);
        listToAdd.add(100);
        listToAdd.add(1000);

        list.addAll(listToAdd);

        assertEquals(1, list.get(0));
        assertEquals(10, list.get(1));
        assertEquals(100, list.get(2));
        assertEquals(1000, list.get(3));
        assertEquals(4, list.size());
    }

    @Test
    void addByIndex() {
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(1, 5);
        assertEquals(1, list.get(0));
        assertEquals(5, list.get(1));
        assertEquals(2, list.get(2));
        assertEquals(3, list.get(3));
        assertEquals(4, list.size());
    }

    @Test
    void get() {// неинформативный нейминг методов, чекни конвенции по наименовании методов тестов
        list.add(1);
        list.add(2);
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
    }

    @Test
    void deleteFirst() {
        list.add(0);
        list.add(1);
        list.add(2);
        list.deleteFirst(1);
        assertEquals(0, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(2, list.size());
    }

    @Test
    void clear() {
        list.add(0); // у тебя почти в каждом методе идет добавление элементов в лист. Не лучше ли сразу инициализировать какими то значениями этот лист в BeforeEach чтобы избежать дублирования?
        list.add(1);
        list.add(2);
        list.clear();
        assertEquals(0, list.size());
        assertThrows(IndexOutOfBoundsException.class,() -> list.get(0));
    }



    @Test
    void size() {
        list.add(0);
        list.add(1);
        list.add(2);
        assertEquals(3, list.size());
        list.clear();
        assertEquals(0, list.size());
        list.add(1);
        list.add(2);
        list.deleteFirst(1);
        assertEquals(1, list.size());
    }

    @Test
    void subList(){
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        List<?> subList = list.subList(2, 4);
        assertEquals(2, subList.get(0));
        assertEquals(3, subList.get(1));
        assertEquals(2, subList.size());
    }

    @Test
    void toArray(){
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        Integer[] expectedArray = new Integer[]{1,1,1,1,1};
        assertArrayEquals(expectedArray, list.toArray());
    }
}