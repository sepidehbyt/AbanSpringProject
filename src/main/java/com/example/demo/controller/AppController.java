package com.example.demo.controller;

import com.example.demo.annotation.Functional;
import com.example.demo.annotation.SpringClassScanner;
import com.example.demo.domain.Customer;
import com.example.demo.domain.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{table}")
    public @ResponseBody String CREATE(@RequestBody Request request, @PathVariable String table) {

        Functional functional = new Functional(jdbcTemplate);
        response = functional.performCreateQuery(table, request.getParams());
        return response.toString();
    }

    @GetMapping("/{table}/{ID}")
    public @ResponseBody String READ(@PathVariable String table, @PathVariable String ID) {
        Functional functional = new Functional(jdbcTemplate);
        response = functional.performReadQuery(table, ID);
        return response.toString();
    }

    @DeleteMapping("/{table}/{ID}")
    public @ResponseBody String DELETE(@PathVariable String table, @PathVariable String ID) {
        Functional functional = new Functional(jdbcTemplate);
        response = functional.performDeleteQuery(table, ID);
        return response.toString();
    }

    @PostMapping("/{table}/{ID}")
    public @ResponseBody String UPDATE(@RequestBody Request request, @PathVariable String table, @PathVariable String ID) {

        Functional functional = new Functional(jdbcTemplate);
        response = functional.performUpdateQuery(table, ID, request.getParams());
        return response.toString();
    }

}