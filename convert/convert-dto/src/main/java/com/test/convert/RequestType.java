package com.test.convert;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class RequestType {

    @XmlAttribute(name = "НомЗапр")
    private String numberRequest;
    @XmlAttribute(name = "ДатаЗапр")
    private String dateRequest;
    @XmlAttribute(name = "КодОснов")
    private String code;
    @XmlAttribute(name = "ВидЗапр")
    private String view;
    @XmlAttribute(name = "ОсновЗапр")
    private String osn;
    @XmlAttribute(name = "ТипЗапр")
    private String type;
    @XmlAttribute(name = "ДатаПоСост")
    private String date;
    @XmlElement(name = "СвНО")
    private SVNOType svno;
    @XmlElement(name = "СвБанк")
    private BankType bank;
    @XmlElement(name = "СвПл")
    private SVPLType svpl;
    @XmlElement(name = "Руководитель")
    private ChiefType chief;

    public RequestType() {
    }
}
