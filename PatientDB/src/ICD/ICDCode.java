/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ICD;

import patientdb.data.FileExtensionFilter;
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
public class ICDCode {

    private final String CLASS = "Class";
    private final String PREFEERED = "preferred";
    private final String CODE = "code";
    private final String RUBRIC = "Rubric";
    private final String LABEL = "Label";
    private final String KIND = "kind";
    private final List<ICDModel> items = new ArrayList<>();
    private String version;
    private final String path;
    private final String filterPattern;

    public ICDCode(String path, String filterPattern){
        this.path = path;
        this.filterPattern = filterPattern;
    }
    
    /**
     * @TODO XML-File import ändern, so dass nur geschaut wird nach einer
     * XML-Datei
     */
    public void readFile() {
        try {
            File folder = new File(this.path);
            File[] test = folder.listFiles(new FileExtensionFilter("XML", "xml"));
            if (test.length == 1) {
                File fXmlFile = test[0];
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName(CLASS);
                for (int i = 0; i < nList.getLength(); i++) {
                    Element node = (Element) nList.item(i);
                    if (node.getAttribute("code").matches(this.filterPattern)) {
                        items.add(new ICDModel(((Element) nList.item(i)).getAttribute(CODE)));
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
            } else {
                throw new IllegalArgumentException("Es wurde mehr als eine XML-Datei gefunden!");
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ICDCode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<ICDModel> getItems() {
        return items;
    }

    public String getVersion() {
        return version;
    }

    public ICDModel getItem(String code) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getCode().equals(code)) {
                index = i;
            }
        }
        return items.get(index);
    }
}
