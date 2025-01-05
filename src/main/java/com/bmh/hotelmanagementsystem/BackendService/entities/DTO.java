package com.bmh.hotelmanagementsystem.BackendService.entities;

public class DTO<T> {

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
