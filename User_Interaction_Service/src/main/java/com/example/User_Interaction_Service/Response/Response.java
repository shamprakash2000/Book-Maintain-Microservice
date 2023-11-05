package com.example.User_Interaction_Service.Response;


public class Response {
    private String message;

    private Object responseObject;


    public Response() {
    }

    public Response(Object responseObject) {
        this.responseObject = responseObject;
    }

    public Response(String message) {
        this.message = message;
    }

    public Response(String message, Object responseObject) {
        this.message = message;
        this.responseObject = responseObject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(Object responseObject) {
        this.responseObject = responseObject;
    }
}
