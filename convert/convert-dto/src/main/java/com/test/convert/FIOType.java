package com.test.convert;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

public class FIOType {

    @XmlAttribute(name = "Фамилия")
    private String lastName;
    @XmlAttribute(name = "Имя")
    private String firstName;
    @XmlAttribute(name = "Отчество")
    private String secondName;

    public FIOType() {
    }
}
