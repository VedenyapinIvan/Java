package com.test.convert;

import com.test.convert.config.Student;

//import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.*;

@SpringBootApplication
public class Convert implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Convert.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("... start convert");
    }

    @Autowired
    private ConfigurableApplicationContext context;
    //    @Autowired
//    private DecodeApiGrpc.DecodeApiFutureStub apiAsync;
//    @Autowired
//    @Qualifier("applicationTaskExecutor")
//    private Executor executorPool;

//    private Set<Integer> requests = new ConcurrentSkipListSet<>();

//    @PostConstruct
//    public void postConstruct() {
//        context.registerShutdownHook();
//    }

    public ArrayList<String> files = new ArrayList<>();

    public boolean validateXMLSchema(String xmlPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//            Schema schema = factory.newSchema(new File(xsdPath));

            ClassLoader classLoader = getClass().getClassLoader();

            Schema schema = factory.newSchema(classLoader.getResource("Student.xsd"));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
    }


    @Value(value = "${convert.inDirection}")
    public String inDIR;

    //@Scheduled(fixedDelay = delay)
    @Scheduled(cron = "${convert.createList.cron}")
    public void createList() {
        System.out.println("createList");
        File catalog = new File(inDIR);
        for (File item : catalog.listFiles()) {
            if (item.isDirectory()) {
                System.out.println("Directory:" + item.getName());
                continue;
            } else {
                System.out.println("File:" + item.getName());
                files.add(item.getAbsolutePath());
            }
        }
    }

    @Scheduled(fixedDelay = 3000)
    public void onTimeout_AsyncRequest() {
        if (files.size()>=1) {

            Student unStudent = convertToObject(files.get(1));
            System.out.println(unStudent.toString());
            System.out.println(validateXMLSchema(files.get(1)));

            File oldFile = new File(files.get(1));
            File newFile = new File("C:\\SomeDir2\\"+oldFile.getName());

            oldFile.renameTo(newFile);

            files.remove(1);
            oldFile.delete();

        }
//        File catalog = new File("C:\\SomeDir");
//        for (File item : catalog.listFiles()) {
//            if (item.isDirectory()) {
//                System.out.println("Directory:" + item.getName());
//            } else {
//                System.out.println("File:" + item.getName());
//                Student unStudent = convertToObject(item.getAbsolutePath());
//                System.out.println(unStudent.toString());
//            }
//        }
    }

    public Student convertToObject(String filePath) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Student.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            return (Student) unmarshaller.unmarshal(new File(filePath));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

//        String[] codeValues = new String[]{"АРМЕНИЯ", "АВСТРАЛИЯ", "АВСТРИЯ", "РЯЗАНЬ"};
//        com.sbt.decode.System[] sourceSystem = null; //new String[]{"WAY4", "SCM"};
//
//        DecodeServiceRq.Builder builder = DecodeServiceRq.newBuilder();
//        DecodeServiceRq request = builder
//                .setCatalogName(Catalog.COUNTRY)
//                .setSourceSystem(sourceSystem[new Random().nextInt(sourceSystem.length)])
//                .setTargetSystem("PPRB")
//                .setCodeValue(codeValues[new Random().nextInt(codeValues.length)])
//                .build();
//
//        try {
//            ListenableFuture<DecodeServiceRs> future = apiAsync.getDecodeValue(request);
//
//            Futures.addCallback(
//                    future,
//                    new FutureCallback<DecodeServiceRs>() {
//                        @Override
//                        public void onSuccess(DecodeServiceRs response) {
//                            System.out.println((response.getDecodeValue() == null) ? "null" : "not null");
//                            System.out.println("Code value: " + request.getCodeValue() + " Decode value: " + response.getDecodeValue() + " Status: " + response.getStatus().getStatusCode());
//                        }
//
//                        @Override
//                        public void onFailure(Throwable t) {
//                            System.out.println("... error");
//                            t.printStackTrace();
//                        }
//                    },
//                    executorPool);
//        } catch (Throwable t) {
//        }
}
