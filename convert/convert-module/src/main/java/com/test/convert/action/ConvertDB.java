package com.test.convert.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component
public class ConvertDB {
    private final static Logger logger = LoggerFactory.getLogger(ConvertDB.class);

//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public Boolean insertDB(String id, String loggerName, String type, String message) {
//        try {
//            int i = jdbcTemplate.update("");
//        } catch (Exception e) {
//            logger.info("Record failed insert in DB with error" + e.getMessage());
//            return false;
//
//        }
//        return true;
//    }
}
