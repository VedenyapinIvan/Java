package com.home.decode;

import com.home.decode.dao.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SnpDecode implements CommandLineRunner {

    //@Autowired
    //DataSource dataSource;

    @Autowired
    CustomerRepository customerRepository;

    public static void main(String[] args) {
        SpringApplication.run(SnpDecode.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Display count row with rqUID=9244bd9aabaf48a28752e62461f0f011: " + customerRepository.findAll());

        System.out.println("Done!");

        // exit(0);
    }
}

