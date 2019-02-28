package com.test.convert;

import javax.xml.bind.annotation.XmlElement;

public class SVPLType {

    @XmlElement(name = "ПФЛ")
    private PFLType pfl;

    public SVPLType() {
    }

}
