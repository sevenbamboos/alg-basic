package com.samwang.common;

public interface Effect<T> {
    void apply(T t);
}