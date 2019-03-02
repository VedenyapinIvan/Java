package com.test.convert;

import com.test.convert.utils.ConvertReform;
import com.test.convert.utils.ConvertVariables;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.util.ArrayList;

@SpringBootApplication
@EnableScheduling
public class Convert {
    private final static Logger logger = LogManager.getLogger(Convert.class);

    public static void main(String[] args) {
        SpringApplication.run(Convert.class, args);
    }

    @Autowired
    private ConvertVariables var;
    @Autowired
    private ConvertReform reform;

    private static boolean containsName(ArrayList<String> files, String item) {
        for (String file : files) {
            if (file.equals(item))
                return true;
        }
        return false;
    }

    @Scheduled(cron = "${convert.cron.createList}")
    public void createList() {
        logger.info("Start refresh filesList ... ");
        File catalog = new File(var.inDIR);
        for (File item : catalog.listFiles()) {
            if (item.isDirectory()) {
                continue;
            } else {
                if (!containsName(var.fileList, item.getAbsolutePath()))
                    var.fileList.add(item.getAbsolutePath());
            }
        }
        logger.info("Success refresh filesList with size=" + var.fileList.size());
    }

    //TODO make sending email
    @Scheduled(cron = "${convert.cron.sendMail}")
    public void sendMail() {
        logger.info("Start sendMail ... ");
    }

    @Scheduled(fixedDelay = 1000)
    public void loadFile() {
        if (var.fileList.size() != 0) {
            try {
                reform.validateXMLSchemaAndInsert(var.fileList.get(0));
            } catch (Exception e) {
                logger.info("ValidateXMLSchemaAndInsert failed " + e.getMessage());
            }
            finally {
                //TODO with win7 any time failed remove
                System.gc();
            }
            logger.info("File " + var.fileList.get(0) + " remove");
            try {
                new File(var.fileList.get(0)).delete();
            }
            finally {
                System.gc();
                var.fileList.remove(0);
            }
        }
    }
}
