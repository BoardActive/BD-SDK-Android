package com.branddrop.bakit.models;

public class Custom<T> {

    // T stands for "Type"
    private T t;

    public void set(T t) { this.t = t; }

    public T get() { return t; }
}
