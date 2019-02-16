package com.home.decode.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

//        public List<Customer> findAll() {
    public int findAll() {

//        TODO sample for my DB with accout list
//        List<Customer> result = jdbcTemplate.query(
//                "SELECT id, name, email, created_date FROM customer",
//                (rs, rowNum) -> new Customer(rs.getInt("id"),
//                        rs.getString("name"), rs.getString("email"), rs.getDate("created_date"))
//        );

        int result = jdbcTemplate.queryForObject("SELECT count(*) FROM log.eventlog WHERE rq_uid = ?", Integer.class, "");

        return result;

    }

}
