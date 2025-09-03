package com.example.myapplication;


//静态内部类
public class InnerStaticClassSingleton {
    private InnerStaticClassSingleton() {
    }

    public static InnerStaticClassSingleton getInstance() {
        return InnerClass.INSTANCE;
    }

    private static class InnerClass {
        private static final InnerStaticClassSingleton INSTANCE = new InnerStaticClassSingleton();
    }
}
