package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Sepideh on 8/29/2018.
 */
public class Request {
    private Map<String, String> params;

    public Request(){

    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Request(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "Request{" +
                "params=" + params +
                '}';
    }

}
