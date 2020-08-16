# uml-to-sparql-converter

An automated SPARQL query generation tool that parses UML diagrams of design patterns to construct SPARQL queries to mine patterns in source code by incorporating all necessary constraints and features of the pattern. 

## SPARQL query Generation
1. Place the XMI file for the design pattern in the uml-to-sparql-converter/test/uml directory. 
2. Execute PatternUMLParser.java main by passing the filename of the xmi file (exclude .xmi, for example, if the file name is builder.xmi, argument should be "builder"
3. The .rq file will be created inside uml-to-sparql-converter/test/output directory. 


## UML Diagram XMI generation
In order to create the xmi file for a design pattern:
1. Open the .mdj or .mfj file of the design pattern in starUML. 
2. make the necessary changes in the diagram (if any)
3. in starUML, go to File>Export>XMI Export option (for this option to show up, install the xmi extension of starUML from extensions manager, see below)

### Note: 
1. to open .mfj use the File>Import>Fragment option in StarUML
2. To install XMI extension go to Tools>Extensions Manager and search for "XMI". Install the extension and reload starUML. After reload, File>Export window will have XMI Export as an option

