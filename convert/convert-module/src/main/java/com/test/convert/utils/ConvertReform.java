package com.test.convert.utils;

import com.test.convert.Convert;
import com.test.convert.ConvertFile;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

@Component
public class ConvertReform {
    private final static Logger logger = LogManager.getLogger(Convert.class);

    public boolean validateXMLSchema(String xmlPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(getClass().getClassLoader().getResource("pattern.xsd"));

            schema.newValidator().validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            logger.error("ValidateXMLSchema failed " + e.getMessage());
            return false;
        }
        return true;
    }

    public ConvertFile convertToObject(String filePath) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ConvertFile.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (ConvertFile) unmarshaller.unmarshal(new File(filePath));
        } catch (JAXBException e) {
            logger.error("ConvertToObject failed " + e.getMessage());
        }
        return null;
    }
}
