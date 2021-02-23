package com.uw.css.xmi_parser;

import com.sdmetrics.model.*;
import com.sdmetrics.util.XMLParser;
import com.uw.css.utils.Utils;

import java.io.*;
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
        parseFiles("metamodel2.xml", "xmiTrans2_0_visual_paradigm.xml",
                filename);
    }

    public void saveOutputAsText(String query, String filename){
        try {
            //create an print writer for writing to a file
            PrintWriter out = new PrintWriter(new FileWriter(Utils.TEST_OUTPUT_DIR + filename));

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
        String directory = "C:\\Users\\actsdev\\uml-to-sparql-converter\\test\\uml";
        generateRQandReport(directory);






    }

    private static void generateRQandReport(String directory){
        HashMap<String, Integer> report = new HashMap<>();
        for(File f : new File(directory).listFiles()){
            String name = f.getName();
            if(name.indexOf(".xmi") != -1){
                parseFile(name.substring(0, name.indexOf(".xmi")), report);
                System.out.println("Filename: " + name);
            }
        }
        printReport(report);
        writeReport(directory, report);
    }

    private static void printReport(HashMap<String, Integer> report){
        System.out.println("-----Num RDF Lines Report-----");
        for(String key : report.keySet()){
            System.out.println(key + " : " + report.get(key) + " lines");
        }
        System.out.println("-----End Report-----");
    }
    private static void writeReport(String dir, HashMap<String, Integer> report){
        File f = new File(dir + "RDFLineReport.csv");
        try{
            f.createNewFile();
        }catch (Exception e){};

        try{
            PrintStream stream = new PrintStream(f);
            stream.println("Filename, numRDFLines");
            for(String key : report.keySet()){
                stream.println(key + "," + report.get(key));
            }
        }catch (FileNotFoundException e){System.out.println("Unable to write report file");};
        System.out.println("Report written to " + f.getName());
    }

    private static void parseFile(String filenameIn, HashMap<String, Integer> report){
        PatternUMLParser patternUMLParser = new PatternUMLParser();
        ModelElementResolverService modelElementResolverService = new ModelElementResolverService();
        try {
            String filename = filenameIn;
//            String filename = args[0];
            patternUMLParser.parseTestXMIFile(filename.concat(".xmi"));
//            patternUMLParser.parseTestXMIFile(filename);
            SparqlQuery query = new SparqlQuery();
            Boolean suppressVisibility = true;

//            patternUMLParser.model.getMetaModel();
            Iterator<ModelElement> iterator = patternUMLParser.model.iterator();
            List<ModelElement> fromActivations = new ArrayList<>();
            List<ModelElement> toActivations = new ArrayList<>();

//            List<ModelElement> queryPackage = patternUMLParser.model.getElements(new MetaModelElement("package", null));
            while (iterator.hasNext()){
                ModelElement modelElement = iterator.next();
                MetaModelElement type = modelElement.getType();
                System.out.println(modelElement.getName() +" "+ type.getName());
                if(modelElement.getXMIID() != null){
                    PatternUMLParser.entityMap.put(modelElement.getXMIID(),modelElement);

                    if(modelElement.getType().getName().equals("fromActivation")){
                        fromActivations.add(modelElement);
                    }else{
                        if(modelElement.getType().getName().equals("toActivation")){
                            toActivations.add(modelElement);
                        }
                    }
                }
                query.components = modelElementResolverService.resolveComponents(query.components,modelElement, suppressVisibility);
                query.relationshipItems = modelElementResolverService.resolveRelations(query.relationshipItems,modelElement);
            }
            modelElementResolverService.getInteractionDiagramRelations(fromActivations,toActivations,query.relationshipItems);
            query.constructQuery();
//            patternUMLParser.saveOutputAsText(query.query,filename.replaceFirst(".xmi",".rq"));
            patternUMLParser.saveOutputAsText(query.query,filename.concat(".rq"));
            report.put(filenameIn, query.getRDFLines());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
