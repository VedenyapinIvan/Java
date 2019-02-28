package com.test.convert;

import javax.xml.bind.annotation.XmlAttribute;

public class BankType {

    @XmlAttribute(name = "РегНом")
    private String regionNum;
    @XmlAttribute(name = "НомФил")
    private String filialNum;
    @XmlAttribute(name = "БИК")
    private String bic;
    @XmlAttribute(name = "НаимБанк")
    private String name;
    @XmlAttribute(name = "ИННБанк")
    private String inn;
    @XmlAttribute(name = "КППБанк")
    private String kpp;

    public BankType() {
    }

}
