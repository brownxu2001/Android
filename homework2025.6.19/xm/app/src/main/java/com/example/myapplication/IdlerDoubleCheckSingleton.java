package com.example.myapplication;


//双重锁
public class IdlerDoubleCheckSingleton {
    private static volatile IdlerDoubleCheckSingleton instance;

    private IdlerDoubleCheckSingleton() {
    }

    public static IdlerDoubleCheckSingleton getInstance() {
        if (instance == null) {
            synchronized (IdlerDoubleCheckSingleton.class) {
                if (instance == null) {
                    instance = new IdlerDoubleCheckSingleton();
                }
            }
        }
        return instance;
    }
}
