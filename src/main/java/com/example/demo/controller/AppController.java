package com.example.demo.controller;

import com.example.demo.annotation.Functional;
import com.example.demo.domain.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sepideh on 8/29/2018.
 */


@RestController
public class AppController {

    Map<String, String> response = new HashMap<>();

    @Autowired
    JdbcTemplate jdbcTemplate;

    @PostMapping("/")
    public @ResponseBody String respond(@RequestBody Request request){

        Functional functional = new Functional(jdbcTemplate);
        response = functional.performDB(request);

        //Class gholi = request.getClass();

        return decodeResponse(response, request);
    }

    public String decodeResponse(Map<String, String> response, Request request) {

        switch (response.get("code")) {
            case "0": //table problem
                return "the table " + request.getTable() +
                        " does'nt exist in DB !!";
            case "1": //param problem
                return "the parameter " + response.get("reason") +
                        " does'nt exist in the table " + request.getTable() + " !!";
            case "2": //action problem
                return "the action " + request.getAction() +
                        " is not CRUD !!";
            case "3": //insert succeed
                return "SUCCESSFUL insertion :D \n" + request;
            case "4": //read without id or more than two parameters
                return "not an acceptable read request !!";
            case "5": //successfull read
                if(response.keySet().size() > 1) {
                    response.remove("code");
                    return "SUCCESSFUL read :D \n" + response.toString();
                }
                else {
                    return "there is no " + request.getTable() + " with id = " + request.getParams().get("id") + " :(";
                }
            case "6": //no id selected for update or no paramete
                return "your " + request.getAction() + " request is incomplete. \nno id selected or no parameter defined";
            case "7": //successful update
                response.remove("code");
                if(response.isEmpty())
                    return "the " + request.getTable() + " with id = " + request.getParams().get("id") + " doesn't exist !!";
                else
                    return "SUCCESSFUL update :D \n" + response.toString();
            case "8": // create incompleteness
                return "your " + request.getAction() + " request is incomplete. ";
            case "9": //delete without id or more than two parameters
                return "not an acceptable delete request !!";
            case "10": //successful delete
                System.out.println(response);
                return "SUCCESSFUL delete :D \nthe " + request.getTable() + " with id = " + request.getParams().get("id") + " deleted";
            case "11": //no existence delete
                return "the " + request.getTable() + " with id = " + request.getParams().get("id") + " doesn't exist and can't be deleted !!";
        }

        return "";
    }

}