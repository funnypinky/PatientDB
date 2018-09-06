/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ICD10;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author shaesler Read ICD-10 XML File
 */
public class ICD10 {

    private final String CLASS = "Class";
    private final String PREFEERED = "preferred";
    private final String CODE = "code";
    private final String RUBRIC = "Rubric";
    private final String LABEL = "Label";
    private final String KIND = "kind";
    private final List<ICD10Model> items = new ArrayList<>();
    private String version;

    /**
     * @TODO XML-File import ändern, so dass nur geschaut wird nach einer
     * XML-Datei
     */
    public void readFile() {
        try {
            File fXmlFile = new File("data\\ICD10\\icd10gm2018syst_claml_20170922.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName(CLASS);

            for (int i = 0; i < nList.getLength(); i++) {
                Element node = (Element) nList.item(i);
                if (node.getAttribute("code").matches("[A-Z][0-9][0-9][.][0-9]")) {
                    items.add(new ICD10Model());
                    items.get(items.size() - 1).setCode(((Element) nList.item(i)).getAttribute(CODE));
                    NodeList childList = node.getElementsByTagName(RUBRIC);
                    for (int j = 0; j < childList.getLength(); j++) {
                        if (((Element) childList.item(j)).getAttribute(KIND).equalsIgnoreCase(PREFEERED)) {
                            NodeList labelList = ((Element) childList.item(j)).getElementsByTagName(LABEL);
                            items.get(items.size() - 1).setDescription(labelList.item(0).getTextContent());
                        }
                    }
                }
            }

            version = "ICD-10 Version: " + ((Element) (doc.getElementsByTagName("Title").item(0))).getAttribute("version");

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ICD10.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<ICD10Model> getItems() {
        return items;
    }

    public String getVersion() {
        return version;
    }

}
