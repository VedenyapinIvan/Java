package com.test.convert;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Файл")
public class ConvertFile {

    @XmlAttribute(name = "ИдЭС")
    private String id;
    @XmlAttribute(name = "ТипИнф")
    private String type;
    @XmlAttribute(name = "ТелОтпр")
    private String phone;
    @XmlAttribute(name = "ДолжнОтпр")
    private String post;
    @XmlAttribute(name = "ФамОтпр")
    private String lastName;
    @XmlAttribute(name = "ВерсФорм")
    private String version;
    @XmlElement(name = "ЗАПНОНАЛИЧ")
    private RequestType request;

    public ConvertFile() {
    }
}
