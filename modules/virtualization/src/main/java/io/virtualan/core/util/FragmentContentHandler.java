package io.virtualan.core.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class FragmentContentHandler extends DefaultHandler {

    private String xPath = "/";
    private XMLReader xmlReader;
    private FragmentContentHandler parent;
    private StringBuilder characters = new StringBuilder();
    private Map<String, Integer> elementNameCount = new HashMap<>();
    private List<String>  xPaths;

    public FragmentContentHandler(List<String>  xPaths, XMLReader xmlReader) {
        this.xmlReader = xmlReader;
        this.xPaths = xPaths;
    }

    private FragmentContentHandler(List<String> xPaths, String xPath, XMLReader xmlReader, FragmentContentHandler parent) {
        this(xPaths, xmlReader);
        this.xPath = xPath;
        this.parent = parent;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        Integer count = elementNameCount.get(qName);
        if(null == count) {
            count = 1;
        } else {
            count++;
        }
        elementNameCount.put(qName, count);
        String childXPath = xPath + '/' + qName + '[' + count + ']';

        int attsLength = atts.getLength();
        for(int x=0; x<attsLength; x++) {
            xPaths.add(childXPath + "[@" + atts.getQName(x) + "='" + atts.getValue(x) + ']');
        }

        FragmentContentHandler child = new FragmentContentHandler(xPaths, childXPath, xmlReader, this);
        xmlReader.setContentHandler(child);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String value = characters.toString().trim();
        if(value.length() > 0) {
            xPaths.add(xPath + "='" + characters.toString() + "'");
        }
        xmlReader.setContentHandler(parent);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        characters.append(ch, start, length);
    }

}