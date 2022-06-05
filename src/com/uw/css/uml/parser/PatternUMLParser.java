package com.uw.css.uml.parser;

import com.sdmetrics.model.*;
import com.sdmetrics.util.XMLParser;
import com.uw.css.utils.Utils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.*;

public class PatternUMLParser {
    private static boolean includeSequenceDiagramRelations = true;
    private static boolean includeAssociationRelations = false;
    private static boolean includeAnnotationRelations = false;
    private static boolean isFileInput;
    String dirBase = Utils.UTIL_DIR;
    String umlDirBase= Utils.TEST_UML_DIR;
    MetaModel mm;
    XMLParser parser;
    Model model;
    XMITransformations trans;
    XMIReader reader;
    String outputDir;
    static Map<String,ModelElement> entityMap = new HashMap<>();
    private static boolean includeMethodParamsInSelect = false;

    public PatternUMLParser(String outputDir) {
        this.outputDir = outputDir;
    }

    private void parseFiles(String meta, String xmitrans, String xmiFilePath)
            throws Exception {
        if (meta != null) {
            mm = new MetaModel();
            parser = new XMLParser();
            String metaPath = getClass().getClassLoader().getResource(dirBase + meta).toString();
            parser.parse(metaPath, mm.getSAXParserHandler());
        }
        if (xmitrans != null) {
            trans = new XMITransformations(mm);
            String metaPath = getClass().getClassLoader().getResource(dirBase + xmitrans).toString();
            parser.parse(metaPath, trans.getSAXParserHandler());
        }
        if (xmiFilePath != null) {
            model = new Model(mm);
            reader = new XMIReader(trans, model);
            parser.parse(xmiFilePath, reader);
        }

    }
    public void parseTestXMIFile(String filepath) throws Exception {
        parser = new XMLParser();
        parseFiles("metamodel2.xml", "xmiTrans2_0_visual_paradigm.xml",
                filepath);
    }

    public void saveOutputAsText(String query, String filename, String outputDir){
        try {
            if(outputDir.isEmpty()){
                outputDir = Utils.TEST_OUTPUT_DIR;
            }
            //create an print writer for writing to a file
            PrintWriter out = new PrintWriter(new FileWriter( outputDir+ filename));

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
        System.out.println("Number of Arguments Received: " + args.length);
        String directory = Utils.TEST_UML_DIR;
        String outputDir = Utils.TEST_OUTPUT_DIR;
        if(args!=null && args.length>0) {
            for (int i = 0; i < args.length; i++) {
//                System.out.println("i: " + i);
//                System.out.println(args[i]);
                if(args[i].equals("--input")){
                    resolveInputType(args[i+1]);
                    directory = args[i+1];
                    i+=1;
                }else if(args[i].equals("--file-input")){
                    isFileInput = true;
                    i+=1;
                }else if(args[i].equals("--directory-input")){
                    isFileInput = false;
                    i+=1;
                }else if(args[i].equals("--output")){
                    outputDir = args[i+1];
                    i+=1;
                }else if(args[i].equals("--no-sequence")){
                    includeSequenceDiagramRelations = false;
                    i+=1;
                }else if(args[i].equals("--include-association")){
                    includeAssociationRelations = true;
                    i+=1;
                }else if(args[i].equals("--include-annotation")){
                    includeAnnotationRelations = true;
                    i+=1;
                }else if(args[i].equals("--include-method-params-select")){
                    includeMethodParamsInSelect = true;
                    i+=1;
                }else if(args[i].equals("--help")){
                    printHelpManual();
                    return;
                }else {
                    System.out.println("ERROR: illegal option" + args[i]);
                    printHelpManual();
                    return;
                }
            }
        }
        PatternUMLParser patternUMLParser = new PatternUMLParser(outputDir);
        patternUMLParser.generateRQandReport(directory);
    }

    private static void resolveInputType(String input) {
        File file = new File(input);
        boolean exists = file.exists();
        if(!exists){
            System.out.println("ERROR: Input Path does not exist");
        }
        boolean isDirectory = file.isDirectory();
        if(isDirectory){
            isFileInput = false;
            System.out.println("INFO: Input Path resolved as directory");
        }
        boolean isFile =      file.isFile();
        if(isFile){
            System.out.println("INFO: Input Path is as a single file");
            String extension = FilenameUtils.getExtension(input);
            if(!extension.equals("xmi")){
                System.out.println("WARN: Input file is not in XMI format");
            }
            isFileInput = true;
        }
    }

    private static void printHelpManual() {
        System.out.println("============");
        System.out.println("PatternScout");
        System.out.println("============");
        System.out.println("usage:");
        System.out.println("--file-input \t specify if the input is a single XMI File");
        System.out.println("--directory-input \t specify if the input is a directory with multiple XMI Files");
        System.out.println("--input INPUT_PATH");
        System.out.println("--output OUTPUT_PATH \t path to write generated .rq files");
        System.out.println("--help \t print help manual");
        System.out.println("--no-sequence \t specify to NOT include Sequence diagram relationships in generated query");
        System.out.println("--include-association \t specify to include assciation relationships in generated query");
        System.out.println("--include-annotation \t specify to include annotation relationships in generated query");
        System.out.println("--include-method-params-select \t specify to include method parameters in SELECT Clause of the query");
        System.out.println("DEFAULT BEHAVIOR: \t annotation relationships and association relationships are excluded");
    }

    private void generateRQandReportForFile(String file,HashMap<String, Integer> report ){
        System.out.println("====Parsing File: " + file+"====");
        parseFile(file, report);
        System.out.println("====Completed Parsing File: " + file+"====");
    }

    private void generateRQandReport(String input){
        HashMap<String, Integer> report = new HashMap<>();
        if(isFileInput){
            generateRQandReportForFile(input, report);
        }else{
            for(File f : new File(input).listFiles()){
                String name = f.getPath();
                if(name.indexOf(".xmi") != -1){
//                parseFile(name.substring(0, name.indexOf(".xmi")), report);
                    generateRQandReportForFile(name, report);
                }
            }
        }
        printReport(report);
        writeReport(outputDir, report);
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

    private void parseFile(String filepath, HashMap<String, Integer> report){
        ModelElementResolverService modelElementResolverService = new ModelElementResolverService(includeSequenceDiagramRelations,includeAssociationRelations,includeAnnotationRelations);
        try {
            String filename = FilenameUtils.getName(filepath);
//            String filename = args[0];
            parseTestXMIFile(filepath);
//            patternUMLParser.parseTestXMIFile(filename);
            SparqlQuery query = new SparqlQuery(includeMethodParamsInSelect);
            Boolean suppressVisibility = false;

//            patternUMLParser.model.getMetaModel();
            Iterator<ModelElement> iterator = model.iterator();
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
            saveOutputAsText(query.query,filename.concat(".rq"), outputDir);
            report.put(filename, query.getRDFLines());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
