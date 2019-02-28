package com.test.convert;

import javax.xml.bind.annotation.XmlAttribute;

public class SVNOType {

    @XmlAttribute(name = "КодНО")
    private String code;
    @XmlAttribute(name = "НаимНО")
    private String name;

    public SVNOType() {
    }

}
