# uml-to-sparql-converter

An automated SPARQL query generation tool that parses UML diagrams of design patterns to construct SPARQL queries to mine patterns in source code by incorporating all necessary constraints and features of the pattern. 

![Approach Overview](https://github.com/jeffyjahfar/uml-to-sparql-converter/blob/master/approachOverview.png)

## SPARQL query Generation
1. Place the XMI file for the design pattern in the uml-to-sparql-converter/test/uml directory. 
2. Execute PatternUMLParser.java main by passing the filename of the xmi file (exclude .xmi, for example, if the file name is builder.xmi, argument should be "builder"
3. The .rq file will be created inside uml-to-sparql-converter/test/output directory. 
4. To suppress visibility constraints, set suppressVisibility=True


## UML Diagram XMI generation
In order to create the xmi file for a design pattern from StarUML:
1. Open the .mdj or .mfj file of the design pattern in starUML. 
2. make the necessary changes in the diagram (if any)
3. in starUML, go to File>Export>XMI Export option (for this option to show up, install the xmi extension of starUML from extensions manager, see below)

In order to create the xmi file for a design pattern from Visual Paradigm:
1. Open the .vpp project containing Class diagram and Sequence diagram in Visual Paradigm
2. Use the Export->XMI option and select the XMI version

### Note: 
1. to open .mfj use the File>Import>Fragment option in StarUML
2. To install XMI extension go to Tools>Extensions Manager and search for "XMI". Install the extension and reload starUML. After reload, File>Export window will have XMI Export as an option

### Usage
`
java -jar uml-to-sparql-converter.jar --help
`

============

PatternScout

============

usage:

`--file-input` 	 specify if the input is a single XMI File

`--directory-input` 	 specify if the input is a directory with multiple XMI Files

`--input INPUT_PATH`

`--output OUTPUT_PATH` 	 path to write generated .rq files

`--help` 	 print help manual

`--no-sequence` 	 specify to NOT include Sequence diagram relationships in generated query

`--include-association` 	 specify to include assciation relationships in generated query

`--include-annotation` 	 specify to include annotation relationships in generated query

`--include-method-params-select` 	 specify to include method parameters in SELECT Clause of the query

DEFAULT BEHAVIOR: 	 annotation relationships and association relationships are excluded

