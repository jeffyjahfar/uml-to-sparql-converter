package com.uw.css.xmi_parser;

import com.uw.css.utils.Relationships;
import org.codeontology.Ontology;

import java.util.List;

public class QueryConstructionService {

    String setPrefix(){
        return "PREFIX woc: <"+ Ontology.WOC + ">\n\n";
    }

    int countLines(String s){
        return s.split("\n").length;
    }

    private int numLines;

    String constructSelectStatement(List<Component> componentList, Boolean distinctResult, String query){
        query = query + "SELECT ";

        if(distinctResult){
            query+="DISTINCT ";
        }
        for(Component component: componentList){
            if(component.isSelectItem){
                query = query.concat("?").concat(component.name).concat(" ");
            }
        }
        return query;
    }

    public String constructWhereStatement(String query, List<RelationshipItem> relationshipItems, List<Component> components){
        query = query.concat("\n WHERE {\n ");
        int selectLines = countLines(query);
        //iteration on components
        for(Component c : components){
            query = query.concat("?").concat(c.name).concat(" ").concat(" a ");
            query = query.concat(resolveWOCType(c.getType())).concat(" .\n");

            //modifier iteration
            if(c.getModifiers() != null){
                for(String modifier: c.getModifiers()){
                    query = query.concat("?").concat(c.name).concat(" ").concat(replaceInlinePrefix(Ontology.MODIFIER_PROPERTY.toString()));
                    query = query.concat(" ").concat(resolveModifiers(modifier)).concat(" .\n");
                }
            }
        }

        //iteration on relations
        for(RelationshipItem relationshipItem: relationshipItems){
            Component fromItem = relationshipItem.getFromItem();
            Component toItem = relationshipItem.getToItem();
            query = query.concat("?").concat(fromItem.name).concat(" ");
            query = query.concat(resolveRelationshipWOCType(relationshipItem.getName())).concat(" ");
            if(toItem!=null) {
                query = query.concat("?").concat(toItem.name).concat(" .\n");
            }else{
                if(relationshipItem instanceof TextSpecificationConstraints){
                    TextSpecificationConstraints textSpecificationConstraints = (TextSpecificationConstraints)relationshipItem;
                    query = query.concat("woc:").concat(textSpecificationConstraints.specification).concat(" .\n");
                }
            }
        }
        int whereLines =countLines(query);
        System.out.println("# RDF Triples in query:" + (whereLines-selectLines));
        numLines = whereLines-selectLines;
        //close where clause
        query = query.concat("\n}");
        return query;
    }

    private String resolveModifiers(String typename){
        switch (typename){
            case "public":
                return replaceInlinePrefix(Ontology.PUBLIC_INDIVIDUAL.toString());
            case "private":
                return replaceInlinePrefix(Ontology.PRIVATE_INDIVIDUAL.toString());
            case "protected":
                return replaceInlinePrefix(Ontology.PROTECTED_INDIVIDUAL.toString());
            case "static":
                return replaceInlinePrefix(Ontology.STATIC_INDIVIDUAL.toString());
            case "abstract":
                return replaceInlinePrefix(Ontology.ABSTRACT_INDIVIDUAL.toString());
            case "final":
                return replaceInlinePrefix(Ontology.ABSTRACT_INDIVIDUAL.toString());
            case "synchronized":
                return replaceInlinePrefix(Ontology.ABSTRACT_INDIVIDUAL.toString());
        }
        return "";
    }

    private String resolveRelationshipWOCType(String typename) {
        if(typename.isEmpty()){
            return "";
        }
        Relationships r = Relationships.valueOf(typename);
        switch (r){
            case generalization:
                return Ontology.EXTENDS_PROPERTY.toString().replace(Ontology.WOC,"woc:");
//            case dependency:
//                return Ontology.DEPENDENCY_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case association:
                return Ontology.DEPENDENCY_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case operation:
                return Ontology.HAS_METHOD_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case constructor:
                return Ontology.HAS_CONSTRUCTOR_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case property:
                return Ontology.HAS_FIELD_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case interfacerealization:
                return Ontology.IMPLEMENTS_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case realization:
                return Ontology.IMPLEMENTS_PROPERTY.toString().replace(Ontology.WOC,"woc:");
            case returnType:
                return replaceInlinePrefix(Ontology.RETURN_TYPE_PROPERTY.toString());
            case parameter:
                return replaceInlinePrefix(Ontology.PARAMETER_PROPERTY.toString());
            case type:
                return replaceInlinePrefix(Ontology.JAVA_TYPE_PROPERTY.toString());
            case references:
                return replaceInlinePrefix(Ontology.REFERENCES_PROPERTY.toString());
            case annotation:
                return replaceInlinePrefix(Ontology.ANNOTATION_PROPERTY.toString());
        }
        return "";
    }

    public String replaceInlinePrefix(String s){
        return s.replace(Ontology.WOC,"woc:");
    }

    public String resolveWOCType(String typename){
        switch (typename){
            case "class":
                return Ontology.CLASS_ENTITY.toString().replace(Ontology.WOC,"woc:");
            case "operation":
                return Ontology.METHOD_ENTITY.toString().replace(Ontology.WOC,"woc:");
            case "interface":
                return Ontology.INTERFACE_ENTITY.toString().replace(Ontology.WOC,"woc:");
            case "property":
                return replaceInlinePrefix(Ontology.FIELD_ENTITY.toString());
//            case "parameter":
//                return replaceInlinePrefix(Ontology.PARAMETER_ENTITY.toString());
            default:
                throw new IllegalStateException("Unexpected value: " + typename);
        }
    }

    public String addFilterStatement(String query, List<Component> components){
        String filter = "FILTER { ";
        for (int i = 0; i < components.size(); i++) {
            for (int j = i+1; j < components.size(); j++) {
                if(components.get(i).getType().equals(components.get(j).getType())){
                    filter += "?"+components.get(i).getName() +" != ?"+components.get(j).getName()+" .\n";
                }
            }
        }
        filter +="}\n";
        return query+filter;
    }

    public int getNumLines(){
        return numLines;
    }
}
