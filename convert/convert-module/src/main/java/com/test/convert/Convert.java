package com.test.convert;

import com.test.convert.action.ConvertDB;
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

//    @Autowired
//    private ConvertDB convertDB;
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

    @Scheduled(cron = "${convert.cron.sendMail}")
    public void sendMail() {
        logger.info("Start sendMail ... ");
    }

    @Scheduled(fixedDelay = 1000)
    public void loadFile() {
        if (var.fileList.size() != 0) {

            File oldFile;
            File newFile;
            if (reform.validateXMLSchema(var.fileList.get(0))) {
                ConvertFile convertFile = reform.convertToObject(var.fileList.get(0));

//                System.out.println("Сообщение валидно");
                oldFile = new File(var.fileList.get(0));
                newFile = new File(var.outDIR + oldFile.getName());

                System.gc();
                oldFile.renameTo(newFile);

                var.fileList.remove(0);
                System.gc();
                oldFile.delete();
            } else {
//                logger.info("File " + var.fileList.get(0) + " is`n valid");
                oldFile = new File(var.fileList.get(0));
                newFile = new File(var.failedDIR + oldFile.getName());

                System.gc();
                oldFile.renameTo(newFile);

                var.fileList.remove(0);

                System.gc();
                oldFile.delete();

            }
        }
    }
}
