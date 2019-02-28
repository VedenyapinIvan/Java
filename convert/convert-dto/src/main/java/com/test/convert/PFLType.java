package com.test.convert;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class PFLType {

    @XmlAttribute(name = "ИННФЛ")
    private String inn;
    @XmlAttribute(name = "ДатаРожд")
    private String birthDate;
    @XmlAttribute(name = "МестоРожд")
    private String birthPlace;
    @XmlAttribute(name = "КодДУЛ")
    private String dul;
    @XmlAttribute(name = "СерНомДок")
    private String idDocument;
    @XmlAttribute(name = "ДатаДок")
    private String date;
    @XmlElement(name = "ФИО")
    private FIOType fio;
    @XmlElement(name = "АдрПлат")
    private AddressType address;

    public PFLType() {
    }
}
