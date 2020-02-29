package com.uw.css.xmi_parser;

import com.sdmetrics.model.*;
import com.sdmetrics.util.XMLParser;
import com.uw.css.utils.Utils;

import java.util.Iterator;

public class PatternUMLParser {
    String dirBase = Utils.UTIL_DIR;
    String umlDirBase= Utils.TEST_UML_DIR;
    MetaModel mm;
    XMLParser parser;
    Model model;
    XMITransformations trans;
    XMIReader reader;

    private void parseFiles(String meta, String xmitrans, String xmi)
            throws Exception {
        if (meta != null) {
            mm = new MetaModel();
            parser = new XMLParser();
            parser.parse(dirBase + meta, mm.getSAXParserHandler());
        }
        if (xmitrans != null) {
            trans = new XMITransformations(mm);
            parser.parse(dirBase + xmitrans, trans.getSAXParserHandler());
        }
        if (xmi != null) {
            model = new Model(mm);
            reader = new XMIReader(trans, model);
            parser.parse(umlDirBase + xmi, reader);
        }

    }
    public void parseTestXMIFile() throws Exception {
        parser = new XMLParser();
        parseFiles("metamodel2.xml", "xmiTrans2_0.xml",
                "proxy_pattern.xmi");
    }

    public static void main(String[] args) {
        PatternUMLParser patternUMLParser = new PatternUMLParser();
        try {
            patternUMLParser.parseTestXMIFile();
//            patternUMLParser.model.getMetaModel();
            Iterator<ModelElement> iterator = patternUMLParser.model.iterator();
            while (iterator.hasNext()){
                ModelElement modelElement = iterator.next();
                MetaModelElement type = modelElement.getType();
                switch (type.getName()){
                    case "baseclass":
                        break;
                    case "class":
                        break;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
