package com.uw.css.xmi_parser;

import com.sdmetrics.model.*;
import com.sdmetrics.util.XMLParser;
import com.uw.css.utils.Utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class PatternUMLParser {
    String dirBase = Utils.UTIL_DIR;
    String umlDirBase= Utils.TEST_UML_DIR;
    MetaModel mm;
    XMLParser parser;
    Model model;
    XMITransformations trans;
    XMIReader reader;
    static Map<String,ModelElement> entityMap = new HashMap<>();
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
    public void parseTestXMIFile(String filename) throws Exception {
        parser = new XMLParser();
        parseFiles("metamodel2.xml", "xmiTrans2_0.xml",
                filename);
    }

    public void saveOutputAsText(String query, String filename){
        try {
            //create an print writer for writing to a file
            PrintWriter out = new PrintWriter(new FileWriter(umlDirBase + filename));

            //output to the file a line
            out.println(query);

            //close the file (VERY IMPORTANT!)
            out.close();
        }
        catch(IOException e1) {
            System.out.println("Error during reading/writing");
        }
    }

    public static void main(String[] args) {
        PatternUMLParser patternUMLParser = new PatternUMLParser();
        ModelElementResolverService modelElementResolverService = new ModelElementResolverService();
        try {
            String filename = "proxy_pattern";
            patternUMLParser.parseTestXMIFile(filename.concat(".xmi"));
            SparqlQuery query = new SparqlQuery();


//            patternUMLParser.model.getMetaModel();
            Iterator<ModelElement> iterator = patternUMLParser.model.iterator();
//            List<ModelElement> queryPackage = patternUMLParser.model.getElements(new MetaModelElement("package", null));
            while (iterator.hasNext()){
                ModelElement modelElement = iterator.next();
                MetaModelElement type = modelElement.getType();
                System.out.println(modelElement.getName() +" "+ type.getName());
                if(modelElement.getXMIID() != null){
                    PatternUMLParser.entityMap.put(modelElement.getXMIID(),modelElement);
                }
                query.components = modelElementResolverService.resolveComponents(query.components,modelElement);
                query.relationshipItems = modelElementResolverService.resolveRelations(query.relationshipItems,modelElement);
            }
            query.constructQuery();
            patternUMLParser.saveOutputAsText(query.query,filename.concat(".txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
