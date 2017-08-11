/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

@Controller
@SpringBootApplication
public class Main {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @RequestMapping("/addTemperature")
    public @ResponseBody
    String addTemperature(@RequestParam("tempIn") String cTempIn, @RequestParam("tempOut") String cTempOut) {
        ObjectMapper mapper = new ObjectMapper();

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            initTemperatureTable(statement);
            statement.executeUpdate(String.format("INSERT INTO temps VALUES (%s, %s, now())", cTempIn, cTempOut));

            ArrayList<TemperatureDTO> listTemperatures = readListTemperatures(statement);

            statement.close();
            connection.close();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(listTemperatures);
        } catch (Exception e) {
            return String.format("{\"status\": false, \"message\": \"%s\"}", e.getMessage());
        }
    }

    @RequestMapping("/getListTemperatures")
    public @ResponseBody
    String getTemperatures() {
        ObjectMapper mapper = new ObjectMapper();

        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            initTemperatureTable(statement);
            ArrayList<TemperatureDTO> listTemperatures = readListTemperatures(statement);

            statement.close();
            connection.close();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(listTemperatures);
        } catch (Exception e) {
            return String.format("{\"status\": false, \"message\": \"%s\"}", e.getMessage());
        }
    }

    @RequestMapping("/dropTemperatureTable")
    public @ResponseBody
    String dropTemperatureTable() {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeQuery("DROP TABLE IF EXISTS temps");

            statement.close();
            connection.close();
            return "base was dropped";
        } catch (Exception e) {
            return String.format("{\"status\": false, \"message\": \"%s\"}", e.getMessage());
        }
    }

    private void initTemperatureTable(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS temps (" +
                "tempIn varchar(10), " +
                "tempOut varchar(10)," +
                "measureDate timestamp)");
    }

    private ArrayList<TemperatureDTO> readListTemperatures(Statement stmt) throws SQLException {
        ArrayList<TemperatureDTO> result = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM temps");
        while (rs.next()) {
            String tempIn = rs.getString("tempIn");
            String tempOut = rs.getString("tempOut");
            String measureDate = rs.getTimestamp("measureDate").toString();
            result.add(new TemperatureDTO(tempIn, tempOut, measureDate));
        }
        return result;
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        if (dbUrl == null || dbUrl.isEmpty()) {
            return new HikariDataSource();
        } else {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            return new HikariDataSource(config);
        }
    }

}