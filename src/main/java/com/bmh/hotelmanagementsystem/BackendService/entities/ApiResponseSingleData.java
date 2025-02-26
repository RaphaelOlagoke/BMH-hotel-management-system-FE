package com.bmh.hotelmanagementsystem.BackendService.entities;

import java.util.List;

public class ApiResponseSingleData<T> {
    private ResponseHeader responseHeader;
    private T data;
    private String error;

    protected long totalElements;
    protected int totalPages;

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

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
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

