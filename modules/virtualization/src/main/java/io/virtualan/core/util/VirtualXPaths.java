package io.virtualan.core.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

@Slf4j
public class VirtualXPaths {

    private VirtualXPaths(){

    }
    public static List<String> readXPaths(String xml) {
        List<String> xpaths = new ArrayList<>();
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(new FragmentContentHandler(xpaths, xr));
            xr.parse(new InputSource(new StringReader(xml)));
        }catch (Exception e) {
            log.warn("unable to parse : {}", e.getMessage());
        }
        return xpaths;
    }
}