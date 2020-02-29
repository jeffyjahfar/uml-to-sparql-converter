package com.uw.css.xmi_parser;

import com.sdmetrics.model.MetaModel;
import com.sdmetrics.model.Model;
import com.sdmetrics.model.XMIReader;
import com.sdmetrics.model.XMITransformations;
import com.sdmetrics.util.XMLParser;
import com.uw.css.utils.Utils;

public class PatternUMLParser {
    String dirBase = Utils.TEST_MODEL_DIR;
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
        parseFiles("UML_2.5.1_metamodel.xmi", "testXMITransformations04.xml",
                "proxy_pattern.xmi");
    }

    public static void main(String[] args) {
        PatternUMLParser patternUMLParser = new PatternUMLParser();
        try {
            patternUMLParser.parseTestXMIFile();
            patternUMLParser.model.getMetaModel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
