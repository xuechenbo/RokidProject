package com.rokid.rkglassdemokotlin.test;

import com.rokid.rkglassdemokotlin.network.Result;
import com.rokid.rkglassdemokotlin.network.UserInfo;

public class FanType {

    public <T> Result<T> getGenerics() {
        return new Result<T>();
    }


    public void MainS() {
        Result<UserInfo> generics = getGenerics();
    }

    // 泛型方法 printArray
    public static <E> int printArray(E[] inputArray) {
        // 输出数组元素
        for (E element : inputArray) {
            System.out.printf("%s ", element);
        }
        System.out.println();
        return 1;
    }

    public static void main(String args[]) {
        // 创建不同类型数组： Integer, Double 和 Character
        Integer[] intArray = {1, 2, 3, 4, 5};
        Double[] doubleArray = {1.1, 2.2, 3.3, 4.4};
        Character[] charArray = {'H', 'E', 'L', 'L', 'O'};

        System.out.println("整型数组元素为:");
        int i = printArray(intArray);
        // 传递一个整型数组

        System.out.println("\n双精度型数组元素为:");
        printArray(doubleArray); // 传递一个双精度型数组

        System.out.println("\n字符型数组元素为:");
        printArray(charArray); // 传递一个字符型数组
    }
}
