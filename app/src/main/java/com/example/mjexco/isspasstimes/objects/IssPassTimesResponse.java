package com.example.mjexco.isspasstimes.objects;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IssPassTimesResponse implements Serializable
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("request")
    @Expose
    private Request request;
    @SerializedName("response")
    @Expose
    private List<Response> response = null;
    private final static long serialVersionUID = -127791107753888737L;

    /**
     * No args constructor for use in serialization
     *
     */
    public IssPassTimesResponse() {
    }

    /**
     *
     * @param response
     * @param message
     * @param request
     */
    public IssPassTimesResponse(String message, Request request, List<Response> response) {
        super();
        this.message = message;
        this.request = request;
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }
}