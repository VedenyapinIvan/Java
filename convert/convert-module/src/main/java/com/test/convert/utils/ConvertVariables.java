package com.test.convert.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ConvertVariables {

    @Value(value = "${convert.direction.in}")
    public String inDIR;
    @Value(value = "${convert.direction.out}")
    public String outDIR;
    @Value(value = "${convert.direction.failed}")
    public String failedDIR;

    public ArrayList<String> fileList = new ArrayList<>();
    public ArrayList<String> fileSuccessList = new ArrayList<>();
    public ArrayList<String> fileFailedList = new ArrayList<>();
}
