package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Sepideh on 8/29/2018.
 */
public class Request {
    private String action;
    private String table;
    private Map<String, String> params;

    public Request(){

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Request(String action, String table, Map<String, String> params) {

        this.action = action;
        this.table = table;
        this.params = params;
    }

    @Override
    public String toString() {
        return "Request{" +
                "action='" + action + '\'' +
                ", table='" + table + '\'' +
                ", params=" + params +
                '}';
    }
}
