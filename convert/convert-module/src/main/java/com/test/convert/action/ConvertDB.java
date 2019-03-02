package com.test.convert.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class ConvertDB {
    private final static Logger logger = LoggerFactory.getLogger(ConvertDB.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Boolean insertDB(String uid, String loggerName, String proc_status, String error_text, String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            jdbcTemplate.update("INSERT INTO CONVERT.logs(uid, event_date, event_logger, proc_status, error_text, message) " +
                    "VALUES (?,?,?,?,?,?)", uid, dateFormat.format(new Date()), loggerName, proc_status, error_text, message);
        } catch (Exception e) {
            logger.info("Record " + uid + " failed insert in DB with error " + e.getMessage());
            return false;
        } finally {
            logger.info("Record " + uid + " success insert in DB ... ");
        }
        return true;
    }
}
