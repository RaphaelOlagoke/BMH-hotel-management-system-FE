package com.bmh.hotelmanagementsystem.BackendService.entities;

import java.util.List;

public class ApiResponse<T> {
    private ResponseHeader responseHeader;
    private List<T> data;
    private String error;

    // Getters and Setters
    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
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

class ResponseHeader {
    private String responseCode;
    private String responseMessage;

    // Getters and Setters
    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    @Override
    public String toString() {
        return "ResponseHeader{" +
                "responseCode='" + responseCode + '\'' +
                ", responseMessage='" + responseMessage + '\'' +
                '}';
    }
}

