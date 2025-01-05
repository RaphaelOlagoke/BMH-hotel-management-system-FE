package com.bmh.hotelmanagementsystem.BackendService.entities;

import java.util.List;

public class ApiResponseSingleData<T> {
    private ResponseHeader responseHeader;
    private T data;
    private String error;

    // Getters and Setters
    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "responseHeader=" + responseHeader +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}

