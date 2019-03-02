package com.test.convert.utils;

import com.test.convert.Convert;
import com.test.convert.ConvertFile;
import com.test.convert.action.ConvertDB;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.charset.StandardCharsets;

@Component
public class ConvertReform {
    private final static Logger logger = LogManager.getLogger(Convert.class);

    @Autowired
    private ConvertDB convertDB;

    public void validateXMLSchemaAndInsert(String xmlPath) {
        String msgError = "";
        logger.info("_______________________________________");
        logger.info("File " + xmlPath + " start process ... ");
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(getClass().getClassLoader().getResource("pattern.xsd"));

            schema.newValidator().validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            msgError = e.getMessage();
            logger.error("ValidateXMLSchema failed " + e.getMessage());
        }

        try {
            ConvertFile convertFile = convertToObject(xmlPath);
            System.gc();
            convertDB.insertDB(convertFile.getId(), logger.getName(), (msgError != "" ? "ERROR" : "SUCCESS"), msgError,
                    FileUtils.readFileToString(new File(xmlPath), String.valueOf(StandardCharsets.UTF_8)));
        } catch (Exception ex) { }
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
