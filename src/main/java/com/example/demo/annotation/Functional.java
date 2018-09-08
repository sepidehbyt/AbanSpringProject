package com.example.demo.annotation;

import com.example.demo.domain.Request;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Functional  {

    JdbcTemplate jdbcTemplate;

    public Functional(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String createInsertQuery(String table, Map<String, String> params) {
        String insertSQL = "";
        insertSQL += "insert into " + table + " (";

        for (String column : params.keySet()) {
            insertSQL += (column+", ");
        }

        insertSQL = insertSQL.substring(0, insertSQL.length() - 2);
        insertSQL += ") values (";

        for (String column : params.keySet()) {
            insertSQL += ("\"" + params.get(column)+ "\"" +", ");
        }

        insertSQL = insertSQL.substring(0, insertSQL.length() - 2);
        insertSQL += ");";

        return insertSQL;
    }

    private String createReadQuery(String table, String ID ){
        String readSQL = "";
        readSQL += "select * from " + table +" where id = \"" + ID +"\";";
        return readSQL;
    }

    private String createUpdateQuery(String table, Map<String, String> params, String ID) {
        String updateSQL = "";
        updateSQL += "update " + table + " set ";

        for (String param : params.keySet()) {
            updateSQL += (param + "=\"" + params.get(param) + "\", ");
        }

        updateSQL = updateSQL.substring(0, updateSQL.length() - 2);
        updateSQL += " where id = \"" + ID +"\";";

        return updateSQL;
    }

    private String createDeleteQuery(String table,String ID) {
        String deleteSQL = "";
        deleteSQL += "delete from " + table;

        deleteSQL += " where id = \"" + ID +"\";";

        return deleteSQL;
    }

    public Map<String, String> performCreateQuery(String table, Map<String, String> params) {

        Map<String, String> classes = new SpringClassScanner().findAnnotatedClasses("com.example.demo.domain"); // {Customer=Human}
        Map<String, String> response = new HashMap<>();
        table = classes.get(table);

        String insertSQL = createInsertQuery(table, params);
        System.out.println(insertSQL);
        try {
            jdbcTemplate.execute(insertSQL);
            response.put("status", "success");
            jdbcTemplate.query("SELECT LAST_INSERT_ID();", new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    response.put("id", rs.getString(1));
                    return "";
                }
            });
        }
        catch (DataAccessException e) {
            response.put("status", "failure");
        }
        return response;
    }

    public Map<String, String> performReadQuery(String table,String ID) {

        Map<String, String> classes = new SpringClassScanner().findAnnotatedClasses("com.example.demo.domain"); // {Customer=Human}
        Map<String, String> response = new HashMap<>();
        table = classes.get(table);

        String readSQL = createReadQuery(table, ID);
        System.out.println(readSQL);

        try {
            List<Map<String, Object>> list = jdbcTemplate.queryForList(readSQL);
            for (Map<String, Object> row : list) {
                Map tmp = new HashMap(row);
                tmp.keySet().removeAll(response.keySet());
                response.putAll(tmp);
                System.out.println(row);
            }

            //System.out.println(response + " " + list.isEmpty() + " " + list.size() + " " + list);

            if(list.isEmpty())
                response.put("status", "empty");
            else
                response.put("status", "success");

        }
        catch (DataAccessException e) {
            response.put("status", "failure");
        }
        return response;
    }

    public Map<String, String> performUpdateQuery(String table, String ID, Map<String, String> params) {

        Map<String, String> classes = new SpringClassScanner().findAnnotatedClasses("com.example.demo.domain"); // {Customer=Human}
        Map<String, String> response = new HashMap<>();
        table = classes.get(table);

        String updateSQL = createUpdateQuery(table, params, ID);
        System.out.println(updateSQL);

        String readSQL = createReadQuery(table, ID);
        System.out.println(readSQL);

        try {
            jdbcTemplate.execute(updateSQL);
            response = performReadQuery(table, ID);
            response.put("status", "success");
        }
        catch (DataAccessException e) {
            response.put("status", "failure");
        }
        return response;
    }

    public Map<String, String> performDeleteQuery(String table,String ID) {

        Map<String, String> classes = new SpringClassScanner().findAnnotatedClasses("com.example.demo.domain"); // {Customer=Human}
        Map<String, String> response = new HashMap<>();
        table = classes.get(table);

        String deleteSQL = createDeleteQuery(table, ID);

        try {

            jdbcTemplate.execute(deleteSQL);
            response.put("status", "success");
            return response;

        }
        catch (DataAccessException e) {
            response.put("status", "failure");
        }
        return response;
    }

}
