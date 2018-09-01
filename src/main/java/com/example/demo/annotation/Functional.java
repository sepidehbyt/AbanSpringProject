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

    public Map<String, String> performDB(Request request) {

        Map<String, String> response = new HashMap<>();
        Map<String, String> params = request.getParams();
        Map<String, String> classes;

        String action = request.getAction().toLowerCase();
        String table = request.getTable().toLowerCase();
        List<String> columns = getColumnsOfDB(request.getTable());

        classes = new SpringClassScanner().findAnnotatedClasses("com.example.demo.domain"); // {Customer=Human}

        if(classes.keySet().contains(table)) {
            response.put("code", "0");
            return response;
        }

        if(!action.equals("create") && !action.equals("read") && !action.equals("update") && !action.equals("delete")) {
            response.put("code", "2");
            return response;
        }

        for (String param : params.keySet()) {
            if (!columns.contains(param)) {
                response.put("code", "1");
                response.put("reason", param);
                return response;
            }
        }

        switch (request.getAction().toLowerCase()) {
            case "create":

                if(params.keySet().contains("id")) {
                    response.put("code", "8");
                    return response;
                }

                String insertSQL = createInsertQuery(table, params);
                System.out.println(insertSQL);
                jdbcTemplate.execute(insertSQL);
                response.put("code", "3");
                jdbcTemplate.query("SELECT LAST_INSERT_ID();", new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        response.put("ID", rs.getString(1));
                        return "";
                    }
                });


                return response;

            case "read":

                if(params.keySet().size() > 1 || !params.keySet().contains("id")) {
                    response.put("code", "4");
                    return response;
                }

                String readSQL = createReadQuery(table, params, columns);
                System.out.println(readSQL);
                response.put("code", "5");
                jdbcTemplate.query(readSQL, new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        for (int i = 1; i < columns.size(); i++) {
                            response.put(columns.get(i), rs.getString(i));
                        }
                        return "";
                    }
                });
                return response;

            case "update":

                if(!params.keySet().contains("id") || params.keySet().size() <= 1) {
                    response.put("code", "6");
                    return response;
                }

                String updateSQL = createUpdateQuery(table, params);
                System.out.println(updateSQL);
                response.put("code", "7");
                jdbcTemplate.execute(updateSQL);
                readSQL = createReadQuery(table, params, columns);
                jdbcTemplate.query(readSQL, new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        for (int i = 1; i < columns.size(); i++) {
                            response.put(columns.get(i), rs.getString(i));
                        }
                        return "";
                    }
                });
                return response;

            case "delete":

                if(params.keySet().size() > 1 || !params.keySet().contains("id")) {
                    response.put("code", "9");
                    return response;
                }

                readSQL = createReadQuery(table, params, columns);
                System.out.println(readSQL);
                jdbcTemplate.query(readSQL, new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        for (int i = 1; i < columns.size(); i++) {
                            response.put(columns.get(i), rs.getString(i));
                        }
                        return "";
                    }
                });
                if (response.keySet().size() <= 1) {
                    response.put("code", "11");
                }
                else {
                    response.put("code", "10");
                    String deleteSQL = createDeleteQuery(table, params);
                    jdbcTemplate.execute(deleteSQL);
                }
                return response;
        }

        return response;

    }

    public List<String> getColumnsOfDB(String table) {

        List<String> data;

        data = jdbcTemplate.query("show columns from " + table +  ";", new RowMapper<String>(){
            public String mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                return rs.getString(1);
            }
        });

        return data;
   }

    public boolean checkForTable(String table) {

        return (boolean)jdbcTemplate.query("show tables;", new ResultSetExtractor<Object>() {

            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs.next()) {
                    if (rs.getString(1).equalsIgnoreCase(table)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public String createInsertQuery(String table, Map<String, String> params) {
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

    public String createReadQuery(String table, Map<String, String> params, List<String> columns) {
        String readSQL = "";
        readSQL += "select ";

        for (String column : columns) {
            if(!column.equals("id")) {
                readSQL += (column + ", ");
            }
        }

        readSQL = readSQL.substring(0, readSQL.length() - 2);
        readSQL += " from " + table +" where id = \"" + params.get("id") +"\";";

        return readSQL;
    }

    public String createUpdateQuery(String table, Map<String, String> params) {
        String updateSQL = "";
        updateSQL += "update " + table + " set ";

        for (String param : params.keySet()) {
            if(!param.equals("id")) {
                updateSQL += (param + "=\"" + params.get(param) + "\", ");
            }
        }

        updateSQL = updateSQL.substring(0, updateSQL.length() - 2);
        updateSQL += " where id = \"" + params.get("id") +"\";";

        return updateSQL;
    }

    public String createDeleteQuery(String table, Map<String, String> params) {
        String deleteSQL = "";
        deleteSQL += "delete from " + table;

        deleteSQL += " where id = \"" + params.get("id") +"\";";

        return deleteSQL;
    }

}
