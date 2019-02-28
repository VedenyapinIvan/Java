package com.test.convert;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class ChiefType {

    @XmlAttribute(name = "КласЧин")
    private String grai;
    @XmlElement(name = "ФИО")
    private FIOType fio;

    public ChiefType() {
    }
}
