package com.test.convert;

import javax.xml.bind.annotation.XmlAttribute;

public class AddressType {

    @XmlAttribute(name = "Индекс")
    private String index;
    @XmlAttribute(name = "КодРегион")
    private String region;
    @XmlAttribute(name = "Район")
    private String district;
    @XmlAttribute(name = "Город")
    private String city;
    @XmlAttribute(name = "НаселПункт")
    private String locality;

    public AddressType() {
    }

}
