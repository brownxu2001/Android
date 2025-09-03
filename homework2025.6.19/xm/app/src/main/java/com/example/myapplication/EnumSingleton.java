package com.example.myapplication;


//枚举类型
public class EnumSingleton {
    private EnumSingleton() {
    }

    public static EnumSingleton getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        private EnumSingleton instance;

        Singleton() {
            instance = new EnumSingleton();
        }

        public EnumSingleton getInstance() {
            return instance;
        }
    }
}
