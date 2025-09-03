package com.example.myapplication;




//饿汉类
public class BadmashStaticConstantSingleton {
    private static BadmashStaticConstantSingleton instance = new BadmashStaticConstantSingleton();

    private BadmashStaticConstantSingleton() {
    }

    public static BadmashStaticConstantSingleton getInstance() {
        return instance;
    }
}