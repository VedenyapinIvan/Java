package com.test.convert.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ConvertVariables {

    @Value(value = "${convert.direction.in}")
    public String inDIR;

    public ArrayList<String> fileList = new ArrayList<>();
    //TODO use for sending mail
    public ArrayList<String> fileSuccessList = new ArrayList<>();
    public ArrayList<String> fileFailedList = new ArrayList<>();
}
